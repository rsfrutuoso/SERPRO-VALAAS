resource "azurerm_resource_group" "rg" {
  name     = "rg-${var.app_name}"
  location = var.location
}

resource "azurerm_log_analytics_workspace" "law" {
  name                = "law-${var.app_name}"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

resource "azurerm_container_app_environment" "cae" {
  name                       = "cae-${var.app_name}"
  location                   = azurerm_resource_group.rg.location
  resource_group_name        = azurerm_resource_group.rg.name
  log_analytics_workspace_id = azurerm_log_analytics_workspace.law.id
  log_analytics_workspace_customer_id = azurerm_log_analytics_workspace.law.workspace_id
  log_analytics_workspace_shared_key = azurerm_log_analytics_workspace.law.primary_shared_key
}

resource "azurerm_container_registry" "acr" {
  name                = replace(lower("acr${var.app_name}"), "-", "")
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Basic"
  admin_enabled       = true
}

resource "random_password" "postgres_admin" {
  length           = 20
  special          = true
  override_special = "!@#%*?"
}

resource "random_password" "postgres_app" {
  length           = 20
  special          = true
  override_special = "!@#%*?"
}

locals {
  postgres_admin_password = var.postgres_admin_password != null ? var.postgres_admin_password : random_password.postgres_admin.result
  postgres_app_password   = var.postgres_app_password != null ? var.postgres_app_password : random_password.postgres_app.result
}

resource "azurerm_postgresql_flexible_server" "pg" {
  name                   = "pg-${var.app_name}"
  resource_group_name    = azurerm_resource_group.rg.name
  location               = azurerm_resource_group.rg.location
  version                = "16"
  administrator_login    = var.postgres_admin_username
  administrator_password = local.postgres_admin_password
  zone                   = "1"
  storage_mb             = 32768
  sku_name               = "B_Standard_B1ms"
  backup_retention_days  = 7

  public_network_access_enabled = true
}

resource "azurerm_postgresql_flexible_server_database" "app_db" {
  name      = var.postgres_database_name
  server_id = azurerm_postgresql_flexible_server.pg.id
  collation = "en_US.utf8"
  charset   = "UTF8"
}

resource "azurerm_container_app" "validator" {
  name                         = "ca-${var.app_name}-validator"
  container_app_environment_id = azurerm_container_app_environment.cae.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  identity {
    type = "SystemAssigned"
  }

  secret {
    name  = "acr-password"
    value = azurerm_container_registry.acr.admin_password
  }

  secret {
    name  = "db-password"
    value = local.postgres_app_password
  }

  registry {
    server   = azurerm_container_registry.acr.login_server
    username = azurerm_container_registry.acr.admin_username
    password_secret_name = "acr-password"
  }

  ingress {
    external_enabled = true
    target_port      = 8080
    traffic_weight {
      percentage      = 100
      latest_revision = true
    }
  }

  template {
    min_replicas = 1
    max_replicas = 2

    container {
      name   = "validator-service"
      image  = "${azurerm_container_registry.acr.login_server}/${var.image_name}:${var.image_tag}"
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "docker"
      }

      env {
        name  = "SPRING_DATASOURCE_URL"
        value = "jdbc:postgresql://${azurerm_postgresql_flexible_server.pg.fqdn}:5432/${azurerm_postgresql_flexible_server_database.app_db.name}?sslmode=require"
      }

      env {
        name  = "SPRING_DATASOURCE_USERNAME"
        value = var.postgres_app_username
      }

      env {
        name        = "SPRING_DATASOURCE_PASSWORD"
        secret_name = "db-password"
      }

      env {
        name  = "SERVER_PORT"
        value = "8080"
      }
    }
  }
}
