import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import io.gatling.javaapi.http.HttpDsl.status
import java.time.Duration

@Suppress("HttpUrlsUsage")
class RecordedSimulation : Simulation() {
    init {

        val httpProtocol = http
            .baseUrl("http://computer-database.gatling.io")
            .inferHtmlResources()
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
            .acceptEncodingHeader("gzip, deflate")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .upgradeInsecureRequestsHeader("1")
            .userAgentHeader("Mozilla/5.0 (X11; Linux x86_64; rv:105.0) Gecko/20100101 Firefox/105.0")

        val feeder = csv("search.csv").random()
        val search = exec(http("Home").get("/"))
            .pause(1)
            .feed(feeder)
            .exec(
                http("Search").get("/computers?f=#{searchCriterion}")
                    .check(
                        css("a:contains('#{searchComputerName}')", "href")
                            .saveAs("computerUrl")
                    )
            )
            .pause(1)
            .exec(http("Select").get("#{computerUrl}"))
            .pause(3)

        val browse = repeat(5, "n").on(
            exec(http("Page #{n}").get("/computers?p=#{n}"))
                .pause(1)
        )

        val edit =
            exec(http("Form").get("/computers/new"))
                .pause(1)
                .exec(
                    http("Post")
                        .post("/computers")
                        .formParam("name", "Beautiful Computer")
                        .formParam("introduced", "2012-05-30")
                        .formParam("discontinued", "")
                        .formParam("company", "37")
                        .check(status().shouldBe(200))
                )

        val users = scenario("Users").exec(search, browse)
        val admins = scenario("Admins").exec(search, browse, edit)

        setUp(
            users.injectOpen(
                nothingFor(1.seconds()),
                atOnceUsers(3),
                rampUsers(5).during(5.seconds()),
                constantUsersPerSec(3.0).during(30.seconds()),
                rampUsersPerSec(5.0).to(6.0).during(3.seconds()),
            ).protocols(httpProtocol),
            admins.injectOpen(rampUsers(10).during(1.minutes())).protocols(httpProtocol)
        )
    }

    private fun Int.minutes() = Duration.ofMinutes(this.toLong())
    private fun Int.seconds() = Duration.ofSeconds(this.toLong())
}