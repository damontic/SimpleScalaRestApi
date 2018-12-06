package co.s4n.routes

import com.lonelyplanet.prometheus.PrometheusResponseTimeRecorder
import com.lonelyplanet.prometheus.api.MetricsEndpoint

object PrometheusRoutes {
    val prometheusRegistry = PrometheusResponseTimeRecorder.DefaultRegistry
    val metricsEndpoint = new MetricsEndpoint(prometheusRegistry)
    val routes = metricsEndpoint.routes
}
