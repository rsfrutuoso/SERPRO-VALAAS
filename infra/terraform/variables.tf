variable "location" {
  type        = string
  description = "Azure region for the deployment."
  default     = "eastus"
}

variable "app_name" {
  type        = string
  description = "Name used as the base for Azure resources."
  default     = "valaas"
}

variable "image_name" {
  type        = string
  description = "Container image name for the validator service."
  default     = "validator-service"
}

variable "image_tag" {
  type        = string
  description = "Container image tag to deploy."
  default     = "latest"
}

variable "postgres_admin_username" {
  type        = string
  description = "Administrator login for the PostgreSQL Flexible Server."
  default     = "valaasadmin"
}

variable "postgres_app_username" {
  type        = string
  description = "Application username used by the service to connect to PostgreSQL."
  default     = "valaasapp"
}

variable "postgres_database_name" {
  type        = string
  description = "Database name to create for application data."
  default     = "valaas"
}

variable "postgres_admin_password" {
  type        = string
  description = "Administrator password for the PostgreSQL Flexible Server."
  sensitive   = true
  default     = null
}

variable "postgres_app_password" {
  type        = string
  description = "Application password used by the validator service."
  sensitive   = true
  default     = null
}
