package com.dehucka.quartz.plugin.config

import io.ktor.server.config.*
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory


/**
 * Created on 30.07.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
data class SchedulingConfig(
    val config: ApplicationConfig
) {
    var scheduler: Scheduler = StdSchedulerFactory.getDefaultScheduler()
    var schedules: MutableMap<JobDetail, Trigger> = mutableMapOf()

    inline fun <reified T : Job> cron(path: String) {
        val cron = config.property("cron.$path").getString()

        val job: JobDetail = JobBuilder.newJob(T::class.java)
            .withIdentity("$path-job")
            .build()
        val trigger: Trigger = TriggerBuilder.newTrigger()
            .withIdentity("$path-trigger")
            .startNow()
            .withSchedule(CronScheduleBuilder.cronSchedule(cron))
            .build()

        schedules[job] = trigger
    }
}
