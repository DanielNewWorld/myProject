package introduction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;

public class Selentroduction {
	public static void main(String[] args) {
		//System.setProperty("webdriver.chrome.driver", "D:\\Selenium\\chromedriver.exe");
		//WebDriver driver = new ChromeDriver();
		
		System.setProperty("webdriver.gecko.driver", "D:\\Selenium\\geckodriver.exe");
		WebDriver driver = new FirefoxDriver();

		//System.setProperty("webdriver.edge.driver", "D:\\Selenium\\msedgedriver.exe");
		//WebDriver driver = new EdgeDriver();
		
		driver.get("https://www.proflowers.com/");
		//System.out.println(driver.getTitle());
		//System.out.println(driver.getCurrentUrl());

		driver.findElement(By.id("zipCode-LFE47338iQfsxw9H3O8q9")).sendKeys("90006");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
		//try {
			//Thread.sleep(6);
			//driver.findElement(By.id("date-LFE47338iQfsxw9H3O8q9")).sendKeys("04-30-2022");
			//driver.findElement(By.id("date-LFE47338iQfsxw9H3O8q9")).sendKeys("04/30/2022");
			//driver.findElement(By.name("date")).sendKeys("04-30-2022");
			//driver.findElement(By.name("date")).sendKeys("05/4/2022");
		//} catch (InterruptedException e) {
		//	throw new RuntimeException(e);
		//}
		driver.findElement(By.className("button_btn-swag-light__g1SUb")).click();
		//System.out.println(driver.findElement(By.cssSelector("p.error")).getText());
		driver.findElement(By.linkText("Forgout your password")).click();
		driver.findElement(By.xpath("//input[@placeholder='Name']")).sendKeys('Daniel');

		//driver.close(); //закрывает текущее окно
		//driver.quit(); // закрывает все окна и браузер
	}
}