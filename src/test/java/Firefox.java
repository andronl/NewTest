import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by andrei on 06/06/2021.
 */
public class Firefox {
    private WebDriver driver;

    @BeforeEach
    public void firefoxSetup() {

        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability("marionette", true);
        System.setProperty("webdriver.gecko.driver", "./driver/geckodriver");
        driver = new FirefoxDriver();
    }

    @Test
    public void test() throws InterruptedException {

        String fromAirport = "MOW";
        String toAirport = "JFK";
        double wantedPrice = 1300;
        LocalDate fromDate = LocalDate.parse("2021-06-07");
        LocalDate toDate = LocalDate.parse("2021-06-09");

        WebDriverWait wait = new WebDriverWait(driver, 60);
        driver.get(url(fromAirport, toAirport, fromDate, toDate));
        TimeUnit.SECONDS.sleep(50);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("resultsListHeader")));
        String realPrice = driver.findElement(By.xpath("/html/body/div[1]/div[1]/main/div/div[2]/div[2]/div/div[2]/div[1]/div[2]/div[2]/div[2]/div/div/div[2]/div[1]/a[1]/div[1]/div/div/div[2]/span[1]")).getText().substring(1);
        assertTrue(wantedPrice >= Double.parseDouble(realPrice), "there is no ticket fits your price");
    }

    @AfterEach
    public void testTeardown() {
        driver.quit();
    }

    public static void checkDate(LocalDate fromDate, LocalDate toDate) {

        assertAll("Verify date",
                () -> assertTrue(LocalDate.now().compareTo(fromDate) <= 0, "you input wrong date, the date shouldn't be in the past"),
                () -> assertTrue(fromDate.compareTo(toDate) <= 0, "you input wrong date, please try again")
        );
    }

    public static String url(String fromAirport, String toAirport, LocalDate fromDate, LocalDate toDate) {
        checkDate(fromDate, toDate);
        return String.format("https://www.kayak.com/flights/%s-%s/%s/%s?sort=price_a",
                fromAirport, toAirport, fromDate, toDate);
    }
}
