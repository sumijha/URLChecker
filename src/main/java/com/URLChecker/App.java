package com.URLChecker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

public class App {
	public static WebDriver driver;
	public static ChromeOptions options;
	public static CSVRead read;
	public static boolean chkFlag;

	public App() throws IOException {
		read = new CSVRead();
	}

	// ************** METHOD Definitions *************** //

	// Initialized the Chrome Driver and Launches URL
	public void init() {
		System.setProperty("webdriver.chrome.driver",
				System.getProperty("user.dir") + File.separator + "chromedriver");

		options = new ChromeOptions();
		Map<String, Object> prefs = new HashMap<String, Object>();
		options.addArguments("--disable-extensions", "--dns-prefetch-disable");
		options.addArguments("--disable-popup-blocking");
		options.addArguments("--safebrowsing-disable-download-protection");
		prefs.put("profile.default_content_settings.popups", 0);
		prefs.put("safebrowsing.enabled", "true");
		options.setExperimentalOption("prefs", prefs);
		options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
		options.setCapability("chrome.switches", Arrays.asList("--incognito"));
		options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

	}

	public void wait(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException ep) {
			ep.printStackTrace();
		}
	}

	public void launchURL(String inpUrl) throws InterruptedException {
		driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		driver.get(inpUrl);
		wait(2);
	}

	// Check Meta Tags Present
	public String checkMetaTag(int rowNo) throws IOException {
		List<WebElement> lstWEb = new ArrayList<>();
		lstWEb = driver.findElements(By.xpath("//meta[@name='description']"));
		int i = 0;

		if (lstWEb.size() > 0 && lstWEb.size() == 1) {
			for (WebElement iEle : lstWEb) {
				try {
					String strVal = iEle.getAttribute("name");
					if (strVal != null && strVal.equals("description")) {
						read.setFlag(rowNo, 1, "TRUE");
						return iEle.getAttribute("content");
					} else {
						if (i == lstWEb.size() - 1) {
							System.out.println("Not Found");
							read.setFlag(rowNo, 1, "FALSE");
							chkFlag = true;
							return null;
						}
					}

				} catch (NullPointerException exp) {
					System.out.println("Checking..");
					if (i == lstWEb.size() - 1) {
						System.out.println("Not Found");
						read.setFlag(rowNo, 1, "FALSE");
						chkFlag = true;
					}
				}
				i++;
			}
		} else {
			System.out.println("No Meta Tags Found or more than one meta tag exists with Description attribute");
			read.setFlag(rowNo, 1, "FALSE");
			chkFlag = true;
		}
		return "";
	}

	// Check Meta String
	public void checkMetaStringLen(int RowNo) throws IOException {
		String metaDes = checkMetaTag(RowNo);
		try {
			if (metaDes.length() >= 80 && metaDes.length() <= 100) {
				System.out.println("MetaString Matched");
				read.setFlag(RowNo, 2, "TRUE");
			} else {
				System.out.println("MetaString Not Matched");
				read.setFlag(RowNo, 2, "FALSE");
				chkFlag = true;
			}
		} catch (NullPointerException exp) {
			System.out.println("MetaString Not Found");
			read.setFlag(RowNo, 2, "FALSE");
			chkFlag = true;
		}
	}

	// Check Title Present
	public String checkTitlePresent(int rowNo) throws IOException {
		List<WebElement> titleTags = driver.findElements(By.tagName("title"));
		if (titleTags.size() == 1) {
			System.out.println("Title Tag Found");
			read.setFlag(rowNo, 3, "TRUE");
			return driver.getTitle().trim();
		} else {
			System.out.println("Title tag does not exists or there are more than 1 title tag");
			read.setFlag(rowNo, 3, "FALSE");
			chkFlag = true;
			return "";
		}
	}

	// Title Pattern Matcher
	public void verifyTitlePattern(int rowNo) throws IOException {
		String outTitle = checkTitlePresent(rowNo);

		if (outTitle.length() >= 40 && outTitle.length() <= 55) {
			System.out.println("Title Length matched");
			read.setFlag(rowNo, 5, "TRUE");
		} else {
			System.out.println("Title Length Not matched");
			read.setFlag(rowNo, 5, "FALSE");
			chkFlag = true;
		}

		if (read.checkFlag(read.getRegExp(), outTitle)) {
			System.out.println("Title Pattern matched");
			read.setFlag(rowNo, 4, "TRUE");
		} else {
			System.out.println("Title Pattern Not matched");
			read.setFlag(rowNo, 4, "FALSE");
			chkFlag = true;
		}
	}

	// Check H1 Tags
	public void checkH1Tags(int rowNo) throws IOException {
		List<WebElement> allH1Tags = driver.findElements(By.tagName("h1"));

		if (allH1Tags.size() == 1) {
			System.out.println("H1 Count tags Found");
			read.setFlag(rowNo, 6, "TRUE");
			read.setFlag(rowNo, 7, "TRUE");
		} else if (allH1Tags.size() > 1) {
			System.out.println("H1 Tags are more than 1");
			read.setFlag(rowNo, 6, "TRUE");
			read.setFlag(rowNo, 7, "FALSE");
			chkFlag = true;
		} else {
			System.out.println("No h1 tags not found");
			read.setFlag(rowNo, 6, "FALSE");
			read.setFlag(rowNo, 7, "FALSE");
			chkFlag = true;
		}
	}

	// Check Canonical Tags
	public void checkCanonTags(int rowNo) throws IOException {
		List<WebElement> allLnkTags = driver.findElements(By.tagName("link"));
		int i = 0;

		if (allLnkTags.size() > 0) {
			for (WebElement ilnk : allLnkTags) {
				try {
					String val = ilnk.getAttribute("rel");

					if (val != null) {
						if (val.equals("canonical")) {
							System.out.println("Canonical Found");
							read.setFlag(rowNo, 8, "TRUE");
							break;
						} else {
							if (i == allLnkTags.size() - 1) {
								System.out.println("Canonical Not Found");
								read.setFlag(rowNo, 8, "FALSE");
								chkFlag = true;
							}
						}
					} else {
						if (i == allLnkTags.size() - 1) {
							System.out.println("Canonical Not Found");
							read.setFlag(rowNo, 8, "FALSE");
							chkFlag = true;
						}
					}
				} catch (NullPointerException ep) {
					if (i == allLnkTags.size() - 1) {
						System.out.println("Canonical Not Found");
						read.setFlag(rowNo, 8, "FALSE");
						chkFlag = true;
					}
				}
				i++;
			}
		} else {
			System.out.println("No Canonical Tags FOund");
			read.setFlag(rowNo, 8, "FALSE");
			chkFlag = true;
		}
	}

	// Check for Img ALT Empty
	public void chkNoImgWithAltEmpty(int rowNo) throws IOException {
		List<WebElement> allImgTags = driver.findElements(By.xpath("//img[@alt='']"));
		if (allImgTags.size() > 0) {
			System.out.println("Empty Alt tags Exists " + allImgTags.size());
			read.setFlag(rowNo, 10, "FALSE");
		} else {
			System.out.println("No Empty Alt Tags Exists or Image not Found");
			read.setFlag(rowNo, 10, "TRUE");
		}
	}

	// Check No Img with NO ALT
	public void chkNoImgNoAlt(int rowNo) throws IOException {
		int imgWithAlt = driver.findElements(By.xpath("//img[@alt]")).size();
		int totImges = driver.findElements(By.xpath("//img")).size();

		if (totImges > 0) {

			if (imgWithAlt == totImges) {
				System.out.println("Total No of Images matched with Alt Count");
				read.setFlag(rowNo, 9, "TRUE");
			} else {
				System.out.println(
						"Images Count " + totImges + " does not match with IMage with Alt Count " + imgWithAlt);
				read.setFlag(rowNo, 9, "FALSE");
			}
		} else {
			System.out.println("No Images Found");
			read.setFlag(rowNo, 9, "TRUE");
		}

	}

	// This will Close the Browser
	public void closeBrowser() {
		try {
			driver.close();
		} catch (Exception ep) {
			System.out.println("Failed to Close the Browser");
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {

		App app = new App();
		app.init();
		List<String> allurl = read.getURLVals();
		int cnt = 1;

		for (String url : allurl) {
            chkFlag = false;

            app.launchURL(url);
            driver.switchTo().frame(0);
            driver.findElement(By.xpath("//div[@class='recaptcha-checkbox-checkmark']")).click();
            driver.switchTo().defaultContent();
            driver.switchTo().frame("recaptcha challenge");
            driver.findElement(By.xpath("//button[text()='Verify']")).click();
            app.checkMetaStringLen(cnt);
            app.verifyTitlePattern(cnt);
            app.checkCanonTags(cnt);
            app.checkH1Tags(cnt);
            app.chkNoImgNoAlt(cnt);
            app.chkNoImgWithAltEmpty(cnt);
            app.closeBrowser();

            if (chkFlag)
                System.out.println("\n" + url + "  : " + "FAIL" + "\n");
            else
                System.out.println("\n" + url + "  : " + "PASS" + "\n");

            cnt++;
        }
		
		driver.quit();
		System.out.println("End of Program");
	}
}
