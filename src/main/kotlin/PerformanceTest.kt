import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory
import java.lang.String.format
import java.time.Duration
import kotlin.system.measureTimeMillis

fun main() {
    PerformanceTest().run()
    PerformanceTest().run()
    PerformanceTest().run()
}

class PerformanceTest {

    class DatabasePage(private val driver: RemoteWebDriver) {

        @FindBy(id = "searchbox")
        private lateinit var filterInput: WebElement

        init {
            @Suppress("HttpUrlsUsage")
            driver.get("http://computer-database.gatling.io/computers")
            PageFactory.initElements(driver, this)
        }

        fun submitSearch(text: String): DatabasePage {
            filterInput.sendKeys(text)
            filterInput.submit()
            return this
        }

        fun clickEntryByName(name: String): EditPage {
            val rowLink = driver.findElement(By.linkText(name))
            rowLink.click()

            return EditPage(driver)
        }

    }

    class EditPage(private val driver: RemoteWebDriver) {

        @FindBy(id = "name")
        private lateinit var nameTextField: WebElement

        @FindBy(id = "discontinued")
        private lateinit var discontinuedTextField: WebElement

        @FindBy(xpath = "//input[@value=\"Save this computer\"]")
        private lateinit var saveButton: WebElement

        init {
            PageFactory.initElements(driver, this)
        }

        fun enterName(text: String, clear: Boolean = false) {
            if (clear) nameTextField.click()
            nameTextField.sendKeys(text)
        }

        fun enterDiscontinued(text: String) {
            discontinuedTextField.sendKeys(text)
        }

        fun save(): DatabasePage {
            saveButton.click()
            return DatabasePage(driver)
        }
    }

    fun run() = doWithDriver { driver ->
        val editPage = measure("Run Search") {
            val dbPage = DatabasePage(driver)
            dbPage.submitSearch("ms")
            dbPage.clickEntryByName("Amstrad CPC")
        }

        measure("Edit PC") {
            editPage.enterName("Foo", clear = true)
            editPage.enterDiscontinued("Bar")
            editPage.save()
        }
    }

    private fun doWithDriver(block: (RemoteWebDriver) -> Unit) {
        WebDriverManager.firefoxdriver().setup()
        System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true")
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null")
        val driver = FirefoxDriver()
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3))

        try {
            block(driver)
        } finally {
            driver.quit()
        }
    }

}

fun <T> measure(name: String, action: () -> T): T {
    var result: T
    val time = measureTimeMillis { result = action() }
    println("${format("%-12s", name)} - $time")

    return result
}