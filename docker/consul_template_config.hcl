consul {
  address = "local-consul.default.svc.cluster.local:8500"

  retry {
    enabled     = true
    attempts    = 12
    backoff     = "250ms"
    max_backoff = "1m"
  }
}

vault {
  address      = "http://local-vault.default.svc.cluster.local:8200"
  grace        = "5m"
  token        = "some_token"
  unwrap_token = false
  renew_token  = false

  retry {
    enabled     = false
    attempts    = 30
    backoff     = "250ms"
    max_backoff = "5m"
  }
}
