package com.sjtu.yifei.utils

import org.gradle.api.Project
import org.gradle.api.logging.Logger

class Log {

    static Logger logger
    static boolean debug

    static void make(Project project, boolean isdebug) {
        logger = project.logger
        debug = isdebug
    }

    static void i(String tag, String info) {
        if (debug && null != tag && null != info) {
            println tag + " >>> " + info
        }
    }

    static void e(String tag, String error) {
        if (null != tag && null != error && null != logger) {
            logger.error tag + " >>> " + error
        }
    }

    static void w(String tag, String warning) {
        if (debug && null != tag && null != warning && null != logger) {
            logger.warn tag + " >>> " + warning
        }
    }
}
