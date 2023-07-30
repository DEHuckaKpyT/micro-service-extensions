package com.dehucka.quartz.plugin

import com.dehucka.quartz.plugin.config.SchedulingConfig
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*


/**
 * Created on 30.07.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
val Scheduling = createApplicationPlugin(name = "scheduling", "scheduling", { SchedulingConfig(it) }) {
    val scheduler = pluginConfig.scheduler

    for ((job, trigger) in pluginConfig.schedules) {
        scheduler.scheduleJob(job, trigger)
    }

    on(MonitoringEvent(ApplicationStarted)) {
        scheduler.start();
    }

    on(MonitoringEvent(ApplicationStopped)) { application ->
        scheduler.shutdown();

        // Release resources and unsubscribe from events
        application.environment.monitor.unsubscribe(ApplicationStarted) {}
        application.environment.monitor.unsubscribe(ApplicationStopped) {}
    }
}