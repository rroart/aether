terraform {
  required_providers {
    gitlab = {
      source = "gitlabhq/gitlab"
      version = "3.6.0"
    }
  }
}

provider "gitlab" {
    #GITLAB_TOKEN
    #GITLAB_BASE_URL 
    insecure = true
}


resource "gitlab_user" "aether" {
  name             = "Aether Gitlab"
  username         = "aether"
  password         = "aether22"
  email            = "gitlab2@user.create"
  is_admin         = true
  projects_limit   = 4
  can_create_group = false
  is_external      = true
  reset_password   = false
}

resource "gitlab_project" "aetherproject" {
  name        = "aether project"
  description = "Aether project"
  visibility_level = "public"
}

resource "gitlab_project_membership" "aetherprojectmember" {
  project_id   = resource.gitlab_project.aetherproject.id
  user_id      = resource.gitlab_user.aether.id
  access_level = "maintainer"
}
