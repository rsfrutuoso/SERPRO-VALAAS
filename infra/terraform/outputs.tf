output "resource_group_name" {
  value = azurerm_resource_group.rg.name
}

output "container_app_url" {
  value = "https://${azurerm_container_app.validator.latest_revision_fqdn}"
}

output "postgres_fqdn" {
  value = azurerm_postgresql_flexible_server.pg.fqdn
}

output "container_registry_login_server" {
  value = azurerm_container_registry.acr.login_server
}
