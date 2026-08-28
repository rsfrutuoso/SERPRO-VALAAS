@description('Azure region for all resources.')
param location string = resourceGroup().location

@description('Base name used for Azure resources.')
param appName string = 'valaas'

@description('Container image name to deploy.')
param imageName string = 'validator-service'

@description('Container image tag to deploy.')
param imageTag string = 'latest'

@description('PostgreSQL administrator username.')
param postgresAdminUser string = 'valaasadmin'

@description('Application database username.')
param postgresAppUser string = 'valaasapp'

@description('Application database name.')
param postgresDatabaseName string = 'valaas'

@secure()
param postgresAdminPassword string

@secure()
param postgresAppPassword string

var logAnalyticsName = 'law-${appName}'
var containerAppEnvName = 'cae-${appName}'
var containerRegistryName = take(replace(toLower('acr${appName}'), '-', ''), 50)
var postgresServerName = 'pg-${appName}'
var containerAppName = 'ca-${appName}-validator'

resource logAnalytics 'Microsoft.OperationalInsights/workspaces@2022-10-01' = {
  name: logAnalyticsName
  location: location
  properties: {
    retentionInDays: 30
    sku: {
      name: 'PerGB2018'
    }
    features: {
      enableLogAccessUsingOnlyResourcePermissions: true
    }
  }
}

resource containerAppEnvironment 'Microsoft.App/managedEnvironments@2024-03-01' = {
  name: containerAppEnvName
  location: location
  properties: {
    appLogsConfiguration: {
      destination: 'log-analytics'
      logAnalyticsConfiguration: {
        customerId: logAnalytics.properties.customerId
        sharedKey: logAnalytics.listKeys().primarySharedKey
      }
    }
  }
}

resource containerRegistry 'Microsoft.ContainerRegistry/registries@2023-07-01' = {
  name: containerRegistryName
  location: location
  sku: {
    name: 'Basic'
  }
  properties: {
    adminUserEnabled: true
  }
}

resource postgresServer 'Microsoft.DBforPostgreSQL/flexibleServers@2023-12-01' = {
  name: postgresServerName
  location: location
  sku: {
    name: 'B_Standard_B1ms'
    tier: 'Burstable'
  }
  properties: {
    version: '16'
    administratorLogin: postgresAdminUser
    administratorLoginPassword: postgresAdminPassword
    storage: {
      storageSizeGB: 32
    }
    backup: {
      backupRetentionDays: 7
      geoRedundantBackup: 'Disabled'
    }
    network: {
      publicNetworkAccess: 'Enabled'
    }
    availabilityZone: '1'
  }
}

resource postgresDatabase 'Microsoft.DBforPostgreSQL/flexibleServers/databases@2023-12-01' = {
  parent: postgresServer
  name: postgresDatabaseName
  properties: {
    charset: 'UTF8'
    collation: 'en_US.utf8'
  }
}

resource containerApp 'Microsoft.App/containerApps@2024-03-01' = {
  name: containerAppName
  location: location
  properties: {
    managedEnvironmentId: containerAppEnvironment.id
    configuration: {
      ingress: {
        external: true
        targetPort: 8080
        allowInsecure: false
        transport: 'auto'
      }
      registries: [
        {
          server: containerRegistry.properties.loginServer
          username: containerRegistry.listCredentials().username
          passwordSecretRef: 'acr-password'
        }
      ]
      secrets: [
        {
          name: 'acr-password'
          value: containerRegistry.listCredentials().passwords[0].value
        }
        {
          name: 'db-password'
          value: postgresAppPassword
        }
      ]
    }
    template: {
      containers: [
        {
          name: 'validator-service'
          image: '${containerRegistry.properties.loginServer}/${imageName}:${imageTag}'
          resources: {
            cpu: json('0.5')
            memory: '1Gi'
          }
          env: [
            {
              name: 'SPRING_PROFILES_ACTIVE'
              value: 'docker'
            }
            {
              name: 'SPRING_DATASOURCE_URL'
              value: 'jdbc:postgresql://${postgresServer.properties.fullyQualifiedDomainName}:5432/${postgresDatabaseName}?sslmode=require'
            }
            {
              name: 'SPRING_DATASOURCE_USERNAME'
              value: postgresAppUser
            }
            {
              name: 'SPRING_DATASOURCE_PASSWORD'
              secretRef: 'db-password'
            }
            {
              name: 'SERVER_PORT'
              value: '8080'
            }
          ]
        }
      ]
      scale: {
        minReplicas: 1
        maxReplicas: 2
      }
    }
  }
}

output containerAppUrl string = 'https://${containerApp.properties.configuration.ingress.fqdn}'
output postgresFqdn string = postgresServer.properties.fullyQualifiedDomainName
output containerRegistryLoginServer string = containerRegistry.properties.loginServer
