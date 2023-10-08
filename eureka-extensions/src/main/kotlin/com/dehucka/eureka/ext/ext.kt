package com.dehucka.eureka.ext

import com.netflix.appinfo.InstanceInfo
import com.netflix.discovery.EurekaClient


/**
 * Created on 03.09.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
fun EurekaClient.getNextServerFromEureka(virtualHostname: String): InstanceInfo {
    return getNextServerFromEureka(virtualHostname, false)
}