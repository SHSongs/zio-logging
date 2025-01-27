package zio.logging

import zio.test._
import zio.{ Config, ConfigProvider, LogLevel }

object ConsoleLoggerConfigSpec extends ZIOSpecDefault {

  val spec: Spec[Environment, Any] = suite("ConsoleLoggerConfig")(
    test("load valid config") {

      val logFormat =
        "%highlight{%timestamp{yyyy-MM-dd'T'HH:mm:ssZ} %fixed{7}{%level} [%fiberId] %name:%line %message %cause}"

      val configProvider: ConfigProvider = ConfigProvider.fromMap(
        Map(
          "logger/format"                                              -> logFormat,
          "logger/filter/rootLevel"                                    -> LogLevel.Info.label,
          "logger/filter/mappings/zio.logging.example.LivePingService" -> LogLevel.Debug.label
        ),
        "/"
      )

      configProvider.load(ConsoleLoggerConfig.config.nested("logger")).map { _ =>
        assertTrue(true)
      }
    },
    test("load default with missing config") {
      val configProvider: ConfigProvider = ConfigProvider.fromMap(
        Map.empty,
        "/"
      )

      configProvider.load(ConsoleLoggerConfig.config.nested("logger")).map { _ =>
        assertTrue(true)
      }
    },
    test("fail on invalid filter config") {
      val logFormat =
        "%highlight{%timestamp{yyyy-MM-dd'T'HH:mm:ssZ} %fixed{7}{%level} [%fiberId] %name:%line %message %cause}"

      val configProvider: ConfigProvider = ConfigProvider.fromMap(
        Map(
          "logger/format"           -> logFormat,
          "logger/filter/rootLevel" -> "INVALID_LOG_LEVEL"
        ),
        "/"
      )

      configProvider
        .load(ConsoleLoggerConfig.config.nested("logger"))
        .exit
        .map { e =>
          assert(e)(Assertion.failsWithA[Config.Error])
        }
    },
    test("fail on invalid format config") {
      val logFormat =
        "%highlight{%timestamp{yyyy-MM-dd'T'HH:mm:ssZ} %fixed{%level} [%fiberId] %name:%line %message %cause}"

      val configProvider: ConfigProvider = ConfigProvider.fromMap(
        Map(
          "logger/format" -> logFormat
        ),
        "/"
      )

      configProvider
        .load(ConsoleLoggerConfig.config.nested("logger"))
        .exit
        .map { e =>
          assert(e)(Assertion.failsWithA[Config.Error])
        }
    }
  )
}
