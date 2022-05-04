package widgetlocator;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WidgetLocator
{
	private static String[] LOCATORS =           { "tag", "class", "name", "id", "href", "alt", "xpath", "idxpath", "is_button", "location", "area", "shape", "visible_text", "neighbor_text" };
	private Integer[] WEIGHTS =                  { 150,    50,      150,    150,  50,     50,    50,      50,         50,         50,         50,     50,      150,            150 };
	private static Integer[] DISTANCE_FUNCTION = { 0,      1,       0,      0,    1,      1,     1,       1,          0,          3,          2,      2,       1,              4 };
	private static int FIRST_APP_INDEX = 0;
	private static int END_APP_INDEX = FIRST_APP_INDEX;
	private static boolean USE_MULTILOCATOR = true;

	private static Hashtable<String, List<File>> folderHash = new Hashtable<String, List<File>>();
	private static Hashtable<String, Properties> propertiesHash = new Hashtable<String, Properties>();

	private WebDriver webDriver=null;
	private String elementsToExtract="input,textarea,button,select,a,h1,h2,h3,h4,h5,li,span,div,p,th,tr,td,label,svg";
	
	private static String[] OLD_WEB_SITES = {"https://web.archive.org/web/20180702000525if_/https://www.adobe.com/", "https://web.archive.org/web/20170401212838if_/https://www.aliexpress.com/", "https://web.archive.org/web/20170402if_/http://www.amazon.com/",
			"https://web.archive.org/web/20171002003641if_/https://www.apple.com/", "https://web.archive.org/web/20191201234246if_/http://www.bing.com/", "https://web.archive.org/web/20181202045722if_/https://www.chase.com/",
			"https://web.archive.org/web/20180402002511if_/https://www.cnn.com/", "https://web.archive.org/web/20160331202313if_/http://sfbay.craigslist.org/", "https://web.archive.org/web/20191202001310if_/https://www.dropbox.com/?landing=dbv2",
			"https://web.archive.org/web/20180601235610if_/https://www.ebay.com/", "https://web.archive.org/web/20191201233248if_/https://www.espn.com/", "https://web.archive.org/web/20191002005309if_/https://www.etsy.com/",
			"https://web.archive.org/web/20161101030329/https://www.facebook.com/", "https://web.archive.org/web/20180102000534if_/https://www.fidelity.com/", "https://web.archive.org/web/20190201214503if_/https://www.salesforce.com/products/platform/products/force/?d=70130000000f27V&internal=true",
			"https://web.archive.org/web/20180802000242if_/https://www.google.com/", "https://web.archive.org/web/20191201225319if_/https://www.hulu.com/welcome", "https://web.archive.org/web/20151202004335if_/http://www.indeed.com/",
			"https://web.archive.org/web/20180602003550if_/https://www.instagram.com/", "https://web.archive.org/web/20171101205846if_/https://www.instructure.com/", "https://web.archive.org/web/20190401223517if_/https://www.intuit.com/",
			"https://web.archive.org/web/20170802004444if_/https://www.linkedin.com/", "https://web.archive.org/web/20170801235424if_/https://outlook.live.com/owa/", "https://web.archive.org/web/20160602021830if_/http://www.microsoft.com/en-us/",
			"https://web.archive.org/web/201802if_/https://login.microsoftonline.com/", "https://web.archive.org/web/20160101232822if_/https://www.shopify.com/", "https://web.archive.org/web/20161003001630if_/https://www.netflix.com/ca/",
			"https://web.archive.org/web/20181201235804if_/https://www.nytimes.com/", "https://web.archive.org/web/20190301225930if_/https://www.office.com/", "https://web.archive.org/web/20171102031615if_/https://www.okta.com/",
			"https://web.archive.org/web/20170304235734if_/https://www.reddit.com/", "https://web.archive.org/web/20160301234356if_/http://www.twitch.tv/", "https://web.archive.org/web/20170702000250if_/https://twitter.com/",
			"https://web.archive.org/web/20170902000248if_/https://www.walmart.com/", "https://web.archive.org/web/20191002055021if_/https://www.wellsfargo.com/", "https://web.archive.org/web/20170901235350if_/https://www.wikipedia.org/",
			"https://web.archive.org/web/20181101/https://www.yahoo.com/", "https://web.archive.org/web/20190801/https://www.youtube.com/", "https://web.archive.org/web/20170801211941if_/https://www.zillow.com/",
			"https://web.archive.org/web/20160501084828/http://zoom.us/"};

	private static String[] NEW_WEB_SITES = {"https://web.archive.org/web/20201102003024if_/https://www.adobe.com/", "https://web.archive.org/web/20201201235538if_/https://www.aliexpress.com/", "https://web.archive.org/web/20201201if_/https://www.amazon.com/",
			"https://web.archive.org/web/20201201235612if_/https://www.apple.com/", "https://web.archive.org/web/20201201234200if_/https://www.bing.com/", "https://web.archive.org/web/20201202004756if_/https://www.chase.com/",
			"https://web.archive.org/web/20201201235755if_/https://www.cnn.com/", "https://web.archive.org/web/20201202081744if_/https://sfbay.craigslist.org/", "https://web.archive.org/web/20201204000601if_/https://www.dropbox.com/?landing=dbv2",
			"https://web.archive.org/web/20201202000703if_/https://www.ebay.com/", "https://web.archive.org/web/20201202000942if_/https://www.espn.com/", "https://web.archive.org/web/20201201233425if_/https://www.etsy.com/",
			"https://web.archive.org/web/20201201011205/https://www.facebook.com/", "https://web.archive.org/web/20201201211643if_/https://www.fidelity.com/", "https://web.archive.org/web/20201201203858if_/https://www.salesforce.com/products/platform/products/force/?sfdc-redirect=300&bc=WA",
			"https://web.archive.org/web/20201201235949if_/https://www.google.com/", "https://web.archive.org/web/20201202000152if_/https://www.hulu.com/welcome", "https://web.archive.org/web/20201201225703if_/https://www.indeed.com/",
			"https://web.archive.org/web/20201202000011if_/https://www.instagram.com/", "https://web.archive.org/web/20201202000839if_/https://www.instructure.com/", "https://web.archive.org/web/20201202032948if_/https://www.intuit.com/",
			"https://web.archive.org/web/20201202011337if_/https://www.linkedin.com/", "https://web.archive.org/web/20201201235603if_/https://outlook.live.com/owa/", "https://web.archive.org/web/20201201234606if_/https://www.microsoft.com/en-us/",
			"https://web.archive.org/web/20201201if_/https://login.microsoftonline.com/", "https://web.archive.org/web/20201202023130if_/http://myshopify.com/", "https://web.archive.org/web/20201201180555if_/https://www.netflix.com/",
			"https://web.archive.org/web/20201202001128if_/https://www.nytimes.com/", "https://web.archive.org/web/20201201232252if_/https://www.office.com/", "https://web.archive.org/web/20201201231944if_/https://www.okta.com/",
			"https://web.archive.org/web/20201201132702if_/https://www.reddit.com/", "https://web.archive.org/web/20201202000056if_/https://www.twitch.tv/", "https://web.archive.org/web/20201201235946if_/https://twitter.com/",
			"https://web.archive.org/web/20201201235513if_/https://www.walmart.com/", "https://web.archive.org/web/20201201235604if_/https://www.wellsfargo.com/", "https://web.archive.org/web/20201202002020if_/https://www.wikipedia.org/",
			"https://web.archive.org/web/20201202/https://www.yahoo.com/", "https://web.archive.org/web/20201201/https://www.youtube.com/", "https://web.archive.org/web/20201201214009if_/https://www.zillow.com/",
			"https://web.archive.org/web/20201202004452/https://zoom.us/"};

	private Neuron similoOutputNeuron=null;
	private boolean logOn = true;

	public WidgetLocator(String[] args)
	{
		if(args.length>0)
		{
			FIRST_APP_INDEX = string2Int(args[0]);
			END_APP_INDEX = FIRST_APP_INDEX;
		}
		if(args.length>1)
		{
			END_APP_INDEX = string2Int(args[1]);
		}
		similoOutputNeuron=createNetwork();
		locateWebElements();
	}

	private int getLocatorIndex(String locatorName)
	{
		int i=0;
		for (String locator : LOCATORS)
		{
			if(locatorName.equals(locator))
			{
				return i;
			}
			i++;
		}
		return 0;
	}

	private Neuron createNetwork()
	{
		Neuron outputNeuron=new Neuron();
		for (String locator : LOCATORS)
		{
			Neuron inputNeuron=new Neuron();
			inputNeuron.setName(locator);
			int index=getLocatorIndex(locator);
			double weight=((double)WEIGHTS[index])/100;
			outputNeuron.connectNeuron(inputNeuron, weight);
		}
		return outputNeuron;
	}

	private String fromIdeToXPath(String ide)
	{
		if(ide==null)
		{
			return null;
		}
		if(ide.startsWith("xpath:"))
		{
			return ide.substring(6);
		}
		else if(ide.startsWith("id:"))
		{
			String id = ide.substring(3);
			return "//*[@id='"+id+"']";
		}
		else if(ide.startsWith("name:"))
		{
			String name = ide.substring(5);
			return "//*[@id='"+name+"']";
		}
		else if(ide.startsWith("linkText:"))
		{
			String linkText = ide.substring(9);
			return "//*[contains(text(),'"+linkText+"')]";
		}
		else
		{
			return ide;
		}
	}

	private long locateWebElements()
	{
		try
		{
			if(USE_MULTILOCATOR)
			{
				System.setProperty("webdriver.chrome.driver", (new File("bin/drivers/chromedriver.exe")).getAbsolutePath());
				webDriver=new ChromeDriver();
				webDriver.manage().timeouts().setScriptTimeout(300, TimeUnit.SECONDS);
			}

			List<File> apps = getFolders("apps");
			int startAppNo = FIRST_APP_INDEX;
			int endAppNo = apps.size();
			endAppNo = END_APP_INDEX;
			for(int appNo=startAppNo; appNo<=endAppNo; appNo++)
			{
				File app = apps.get(appNo);
				
				logTable("##### Application: " + app.getName());

				List<Locator> targetLocators = new ArrayList<Locator>();
				File targetWidgetsFolder = new File(app, "target_widgets");
				List<File> targetFolderFiles = getFolderFiles(targetWidgetsFolder);

				if(USE_MULTILOCATOR)
				{
					String url=OLD_WEB_SITES[appNo];
					webDriver.get(url);
					delay(10000);

					for (File targetWidget : targetFolderFiles)
					{
						Properties targetWidgetProperty = loadProperties(targetWidget);
						String xPath = targetWidgetProperty.getProperty("xpath", null);
						Locator locator = getLocatorForElement(xPath);
						if(locator!=null)
						{
							String widgetId = targetWidgetProperty.getProperty("widget_id", null);
							addMetadata(locator, "widget_id", widgetId);
							targetLocators.add(locator);
						}
						else
						{
							log("No locator for xpath: " + xPath);
						}
					}

					url=NEW_WEB_SITES[appNo];
					webDriver.get(url);
					delay(10000);
				}

				File candidateWidgetsFolder = new File(app, "candidate_widgets");
				List<File> candidateFolderFiles = getFolderFiles(candidateWidgetsFolder);
				List<Properties> candidateWidgetProperties = new ArrayList<Properties>();
				for (File candidateWidget : candidateFolderFiles)
				{
					Properties candidateWidgetProperty = loadProperties(candidateWidget);
					candidateWidgetProperties.add(candidateWidgetProperty);
				}
				
				if(USE_MULTILOCATOR)
				{
					Boolean[] locatedByOne = new Boolean[targetLocators.size()];
					for(int i=0; i<targetLocators.size(); i++)
					{
						locatedByOne[i] = false;
					}
					String[] xPathLocators = {"xpath", "idxpath", "ide", "robula", "montoto"};
					for(String xPathLocator:xPathLocators)
					{
						int located = 0;
						int notLocated = 0;
						int incorrectlyLocated = 0;
						int targetLocatorNo = 0;
						for(Locator targetLocator:targetLocators)
						{
							String targetXPath = targetLocator.getMetadata(xPathLocator);
							if(xPathLocator.equals("ide"))
							{
								targetXPath = fromIdeToXPath(targetXPath);
							}
							Locator candidateLocator = getXPathLocatorForElement(targetXPath);
							if(candidateLocator!=null)
							{
								// Found a candidate
								String widgetId = targetLocator.getMetadata("widget_id");
								String candidateXPath = getXPathFromWidgetId(widgetId, candidateWidgetProperties);
								Locator correctCandidateLocator = getXPathLocatorForElement(candidateXPath);

								boolean isMatch1=false;
								boolean isMatch2=false;
								String bestCandidateXPath = candidateLocator.getMetadata("xpath");
								if(almostIdenticalXPaths(bestCandidateXPath, candidateXPath))
								{
									isMatch1 = true;
								}
								if(isCorrectWebElement(candidateLocator, correctCandidateLocator))
								{
									isMatch2 = true;
								}
								if(isMatch1 && isMatch2)
								{
									located++;
									locatedByOne[targetLocatorNo] = true;
								}
								else if(isMatch1 || isMatch2)
								{
									located++;
									locatedByOne[targetLocatorNo] = true;
								}
								else
								{
									incorrectlyLocated++;
								}
							}
							else
							{
								// Did not find a candidate
								notLocated++;
							}
							targetLocatorNo++;
						}
						logTable(xPathLocator+":\t"+located+"\t"+notLocated+"\t"+incorrectlyLocated);
					}
					
					int located = 0;
					int notLocated = 0;
					for(Boolean oneLocated:locatedByOne)
					{
						if(oneLocated)
						{
							located++;
						}
						else
						{
							notLocated++;
						}
					}
					logTable("Multilocator:\t"+located+"\t"+notLocated+"\t0");
				}

				List<Locator> candidateLocators = getLocators();

				int located = 0;
				int notLocated = 0;
				int incorrectlyLocated = 0;
				for(Locator targetLocator:targetLocators)
				{
					Locator candidateLocator = similo(targetLocator, candidateLocators);
					if(candidateLocator==null)
					{
						notLocated++;
					}
					else
					{
						String widgetId = targetLocator.getMetadata("widget_id");
						String candidateXPath = getXPathFromWidgetId(widgetId, candidateWidgetProperties);
						Locator correctCandidateLocator = getXPathLocatorForElement(candidateXPath);
						boolean isMatch1=false;
						boolean isMatch2=false;
						String bestCandidateXPath = candidateLocator.getMetadata("xpath");
						if(almostIdenticalXPaths(bestCandidateXPath, candidateXPath))
						{
							isMatch1 = true;
						}
						if(isCorrectWebElement(candidateLocator, correctCandidateLocator))
						{
							isMatch2 = true;
						}
						if(isMatch1 && isMatch2)
						{
							located++;
						}
						else if(isMatch1 || isMatch2)
						{
							located++;
						}
						else
						{
							incorrectlyLocated++;
						}
					}
				}
				logTable("Similo:\t"+located+"\t"+notLocated+"\t"+incorrectlyLocated);
			}
				
			if(USE_MULTILOCATOR)
			{
				webDriver.close();
				webDriver.quit();
			}
			return 0;
		}
		catch (Exception e)
		{
			log("Error: " + e.toString());
			if(webDriver!=null)
			{
				webDriver.close();
				webDriver.quit();
			}
			return 0;
		}
	}

	private String removeLastElement(String xpath)
	{
		int lastIndex = xpath.lastIndexOf('/');
		if(lastIndex > 0)
		{
			return xpath.substring(0, lastIndex);
		}
		return xpath;
	}

	private boolean isCorrectWebElement(Locator candidateLocator, Locator correctCandidateLocator)
	{
		if(candidateLocator==null || correctCandidateLocator==null)
		{
			return false;
		}
		Rectangle candidateRectangle = candidateLocator.getLocationArea();
		Rectangle correctCandidateRectangle = correctCandidateLocator.getLocationArea();
		if(correctCandidateRectangle.contains(candidateRectangle.getCenterX(), candidateRectangle.getCenterY()))
		{
			return true;
		}
		return false;
	}

	private boolean almostIdenticalXPaths(String xpath1, String xpath2)
	{
		if(xpath1 == null || xpath2 == null)
		{
			return false;
		}
		int length1 = xpath1.length();
		int length2 = xpath2.length();
		if(length1 == length2)
		{
			return xpath1.equalsIgnoreCase(xpath2);
		}
		else if(length1 < length2)
		{
			xpath2 = removeLastElement(xpath2);
			return xpath1.equalsIgnoreCase(xpath2);
		}
		else
		{
			xpath1 = removeLastElement(xpath1);
			return xpath1.equalsIgnoreCase(xpath2);
		}
	}

	private String getXPathFromWidgetId(String widgetId, List<Properties> candidateWidgetProperties)
	{
		for(Properties widget:candidateWidgetProperties)
		{
			String candidateWidgetId = widget.getProperty("widget_id", "-1");
			if(widgetId.equals(candidateWidgetId))
			{
				String xpath = widget.getProperty("xpath", null);
				return xpath;
			}
		}
		return null;
	}

	private Locator similo(Locator targetWidget, List<Locator> candidateWidgets)
	{
		for (Locator candidateWidget : candidateWidgets)
		{
			double similarityScore = calcScore(targetWidget, candidateWidget);
			candidateWidget.setScore(similarityScore);
		}
		Locator.setSortOnScore(true);
		Collections.sort(candidateWidgets);
		Locator bestCandidateWidget = candidateWidgets.get(0);
		return bestCandidateWidget;
	}

	private double calcScore(Locator targetWidget, Locator candidateWidget)
	{
		List<Neuron> inputNeurons=similoOutputNeuron.getConnectedNeurons();

		// Set input similarity
		for(Neuron inputNeuron:inputNeurons)
		{
			double similarity=0;
			String targetValue = targetWidget.getMetadata(inputNeuron.getName());
			String candidateValue = candidateWidget.getMetadata(inputNeuron.getName());
			int locatorIndex=getLocatorIndex(inputNeuron.getName());
			int distanceFunction = DISTANCE_FUNCTION[locatorIndex];
			if (distanceFunction == 3 || (targetValue != null && candidateValue != null))
			{
				if(distanceFunction == 1)
				{
					similarity=((double)compareDistance(targetValue, candidateValue, 100))/100;
				}
				else if(distanceFunction == 2)
				{
					similarity=((double)compareIntegerDistance(targetValue, candidateValue, 100))/100;
				}
				else if(distanceFunction == 3)
				{
					// Use 2D distance 
					int x = string2Int(targetWidget.getMetadata("x"));
					int y = string2Int(targetWidget.getMetadata("y"));
					int xc = string2Int(candidateWidget.getMetadata("x"));
					int yc = string2Int(candidateWidget.getMetadata("y"));
					int dx = x - xc;
					int dy = y - yc;
					int pixelDistance = (int)Math.sqrt(dx*dx + dy*dy);
					similarity = ((double)Math.max(100 - pixelDistance, 0))/100;
				}
				else if(distanceFunction == 4)
				{
					similarity=((double)compareNeighborText(targetValue, candidateValue, 100))/100;
				}
				else
				{
					similarity=(double)compareEqual(targetValue, candidateValue, 1);
				}
			}
			candidateWidget.scoreParts.put(inputNeuron.getName(), ""+similarity);
			inputNeuron.setValue(similarity);
		}
		
		similoOutputNeuron.recalculateValue();
		return similoOutputNeuron.getValue();
	}

	private int compareEqual(String t1, String t2, int maxScore)
	{
		if (t1 != null && t2 != null)
		{
			if (t1.equalsIgnoreCase(t2))
			{
				return maxScore;
			}
		}
		return 0;
	}

	private int compareIntegerDistance(String t1, String t2, int maxScore)
	{
		int value1 = string2Int(t1);
		int value2 = string2Int(t2);
		return compareIntegerDistance(value1, value2, maxScore);
	}

	private int compareIntegerDistance(int value1, int value2, int maxScore)
	{
		int distance = Math.abs(value1 - value2);
		int max = Math.max(value1, value2);
		int score = (max - distance) * maxScore / max;
		return score;
	}

	private int compareDistance(String t1, String t2, int maxScore)
	{
		String s1 = t1.toLowerCase();
		String s2 = t2.toLowerCase();

		if (s1.equals(s2))
		{
			return maxScore;
		}

		// Make sure s1 is longer (or equal)
		if (s1.length() < s2.length())
		{
			String swap = s1;
			s1 = s2;
			s2 = swap;
		}

		int editDistance = 0;
		int bigLen = s1.length();
		editDistance = computeDistance(s1, s2);
		if (bigLen == 0)
		{
			return maxScore;
		}
		else
		{
			int score = (bigLen - editDistance) * maxScore / bigLen;
			return score;
		}

	}

	private int computeDistance(String s1, String s2)
	{
		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++)
		{
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++)
			{
				if (i == 0)
				{
					costs[j] = j;
				} else
				{
					if (j > 0)
					{
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1))
						{
							newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						}
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0)
			{
				costs[s2.length()] = lastValue;
			}
		}
		return costs[s2.length()];
	}

	private Properties loadProperties(File file)
	{
		if(propertiesHash.containsKey(file.getName()))
		{
			// File is cached
			return propertiesHash.get(file.getName());
		}
		Properties properties = new Properties();
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
			String str;
			while ((str = in.readLine()) != null)
			{
				String[] split = str.split("=", 2);
				if (split.length == 2)
				{
					properties.put(split[0], split[1]);
				}
			}
			properties.put("filename", file.getName());
			in.close();
			propertiesHash.put(file.getName(), properties);
			return properties;
		}
		catch (Exception e)
		{
			// File not found
			return null;
		}
	}

	private List<File> getFolders(String folderName)
	{
		if(folderHash.containsKey(folderName))
		{
			// File is cached
			return folderHash.get(folderName);
		}
		List<File> files = new ArrayList<File>();
		File folder = new File(folderName);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles)
		{
			if (file.isDirectory())
			{
				files.add(file);
			}
		}

		folderHash.put(folderName, files);
		return files;
	}

	private List<File> getFolderFiles(File folder)
	{
		List<File> files = new ArrayList<File>();
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles)
		{
			if (file.isFile())
			{
				files.add(file);
			}
		}

		return files;
	}

	public static void main(String[] args)
	{
		new WidgetLocator(args);
	}

	private void log(String text)
	{
		if(!logOn)
		{
			return;
		}
		System.out.println(text);
		writeLine("WidgetLocatorResults.txt", text);
	}

	private void logTable(String text)
	{
		if(!logOn)
		{
			return;
		}
		writeLine("WidgetLocatorResultsTable.txt", text);
	}

	private void logLocators(String text)
	{
		if(!logOn)
		{
			return;
		}
		System.out.println(text);
		writeLine("locators.txt", text);
	}

	private void writeLine(String filename, String text)
	{
		String logMessage = text + "\r\n";
		File file = new File(filename);
		try
		{
			FileOutputStream o = new FileOutputStream(file, true);
			o.write(logMessage.getBytes());
			o.close();
		}
		catch (Exception e) {}
	}

	/**
	 * Get all locators that belong to any of the tags in elementsToExtract
	 * @return A list of locators to web elements
	 */
	public List<Locator> getLocators()
	{
		List<Locator> locators=new ArrayList<Locator>();
		
		if(webDriver!=null)
		{
			try
			{
				String javascript = loadTextFile("javascript.js");
				webDriver.manage().timeouts().setScriptTimeout(300, TimeUnit.SECONDS);
				JavascriptExecutor executor = (JavascriptExecutor) webDriver;
				executor.executeScript("window.scrollTo(0, 0)");
				Object object=executor.executeScript(javascript +
					"var result = []; " +
					"var all = document.querySelectorAll('"+elementsToExtract+"'); " +
					"for (var i=0, max=all.length; i < max; i++) { " +
					"    if (elementIsVisible(all[i])) result.push({'tag': all[i].tagName, 'class': all[i].className, 'type': all[i].type, 'name': all[i].name, 'id': all[i].id, 'value': all[i].value, 'href': all[i].href, 'text': all[i].textContent, 'placeholder': all[i].placeholder, 'title': all[i].title, 'alt': all[i].alt, 'x': getXPosition(all[i]), 'y': getYPosition(all[i]), 'width': getMaxWidth(all[i]), 'height': getMaxHeight(all[i]), 'children': all[i].children.length, 'xpath': getXPath(all[i]), 'idxpath': getIdXPath(all[i])}); " +
					"} " +
					" return JSON.stringify(result); ");				
				
				
				String json=object.toString();
				JSONParser parser = new JSONParser();
				JSONArray jsonArray = (JSONArray)parser.parse(json);

				for(int i=0; i<jsonArray.size(); i++)
				{
					JSONObject jsonObject=(JSONObject)jsonArray.get(i);

					String tag=object2String(jsonObject.get("tag"));
					if(tag!=null)
					{
						tag=tag.toLowerCase();
					}
					String className=object2String(jsonObject.get("class"));
					String type=object2String(jsonObject.get("type"));
					String name=object2String(jsonObject.get("name"));
					String id=object2String(jsonObject.get("id"));
					String value=object2String(jsonObject.get("value"));
					String href=object2String(jsonObject.get("href"));
					String text=object2String(jsonObject.get("text"));
					String placeholder=object2String(jsonObject.get("placeholder"));
					String title=object2String(jsonObject.get("title"));
					String alt=object2String(jsonObject.get("alt"));
					String xpath=object2String(jsonObject.get("xpath"));
					String idxpath=object2String(jsonObject.get("idxpath"));
					String xStr=object2String(jsonObject.get("x"));
					String yStr=object2String(jsonObject.get("y"));
					String widthStr=object2String(jsonObject.get("width"));
					String heightStr=object2String(jsonObject.get("height"));

					int x=string2Int(xStr);
					int y=string2Int(yStr);
					int width=string2Int(widthStr);
					int height=string2Int(heightStr);

					if(width>0 && height>0)
					{
						Locator locator=new Locator();

						locator.setLocationArea(new Rectangle(x, y, width, height));

						addMetadata(locator, "tag", tag);
						addMetadata(locator, "class", className);
						addMetadata(locator, "type", type);
						addMetadata(locator, "name", name);
						addMetadata(locator, "id", id);
						addMetadata(locator, "value", value);
						addMetadata(locator, "href", href);
						if(isValidText(text))
						{
							addMetadata(locator, "text", text);
						}
						addMetadata(locator, "placeholder", placeholder);
						addMetadata(locator, "title", title);
						addMetadata(locator, "alt", alt);
						addMetadata(locator, "xpath", xpath);
						addMetadata(locator, "idxpath", idxpath);
						addMetadata(locator, "x", xStr);
						addMetadata(locator, "y", yStr);
						addMetadata(locator, "height", heightStr);
						addMetadata(locator, "width", widthStr);

						int area = width * height;
						int shape = (width * 100) / height;
						addMetadata(locator, "area", ""+area);
 						addMetadata(locator, "shape", ""+shape);

						String visibleText=locator.getVisibleText();
						if(visibleText!=null)
						{
							locator.putMetadata("visible_text", visibleText);
						}
						String isButton=isButton(tag, type, className)?"yes":"no";;
						locator.putMetadata("is_button", isButton);

						locators.add(locator);
					}
				}
				
				for(Locator locator:locators)
				{
					addNeighborTexts(locator, locators);
				}
				
				return locators;
			}
			catch (Exception e)
			{
				return null;
			}
		}

		return null;
	}

	public Locator getLocatorForElement(String elementXPath)
	{
		List<Locator> locators=getLocators();
		if(locators!=null)
		{
			for(Locator locator:locators)
			{
				String xpath=locator.getMetadata("xpath");
				if(xpath.equals(elementXPath))
				{
					Locator locatorAll = getAllLocatorsForElement(elementXPath);
					if(locatorAll!=null)
					{
						locator.putMetadata("ide", locatorAll.getMetadata("ide"));
						locator.putMetadata("robula", locatorAll.getMetadata("robula"));
						locator.putMetadata("montoto", locatorAll.getMetadata("montoto"));
					}
					return locator;
				}
			}
		}
		return null;
	}
	
	public Locator getAllLocatorsForElement(String elementXPath)
	{
		List<Locator> locators=new ArrayList<Locator>();
		
		if(webDriver!=null)
		{
			try
			{
				String javascript = loadTextFile("javascript.js");
				webDriver.manage().timeouts().setScriptTimeout(300, TimeUnit.SECONDS);
				JavascriptExecutor executor = (JavascriptExecutor) webDriver;
				executor.executeScript("window.scrollTo(0, 0)");
				Object object=executor.executeScript(javascript +
					"var result = []; " +
					"var all = []; " +
					"var element = locateElementByXPath('"+elementXPath+"'); " +
					"if (element!=null) all.push(element); " +
					"for (var i=0, max=all.length; i < max; i++) { " +
					"    result.push({'tag': all[i].tagName, 'class': all[i].className, 'type': all[i].type, 'name': all[i].name, 'id': all[i].id, 'value': all[i].value, 'href': all[i].href, 'text': all[i].textContent, 'placeholder': all[i].placeholder, 'title': all[i].title, 'alt': all[i].alt, 'x': getXPosition(all[i]), 'y': getYPosition(all[i]), 'width': getMaxWidth(all[i]), 'height': getMaxHeight(all[i]), 'children': all[i].children.length, 'robula': getRobulaPlusXPath(all[i]), 'montoto': getMonotoXPath(all[i]), 'ide': getSeleniumIDELocator(all[i]), 'xpath': getXPath(all[i]), 'idxpath': getIdXPath(all[i])}); " +
					"} " +
					" return JSON.stringify(result); ");				
				
				
				String json=object.toString();
				JSONParser parser = new JSONParser();
				JSONArray jsonArray = (JSONArray)parser.parse(json);

				if(jsonArray.size()<1)
				{
					return null;
				}
				
				for(int i=0; i<jsonArray.size(); i++)
				{
					JSONObject jsonObject=(JSONObject)jsonArray.get(i);

					String tag=object2String(jsonObject.get("tag"));
					if(tag!=null)
					{
						tag=tag.toLowerCase();
					}
					String className=object2String(jsonObject.get("class"));
					String type=object2String(jsonObject.get("type"));
					String name=object2String(jsonObject.get("name"));
					String id=object2String(jsonObject.get("id"));
					String value=object2String(jsonObject.get("value"));
					String href=object2String(jsonObject.get("href"));
					String text=object2String(jsonObject.get("text"));
					String placeholder=object2String(jsonObject.get("placeholder"));
					String title=object2String(jsonObject.get("title"));
					String alt=object2String(jsonObject.get("alt"));
					String xpath=object2String(jsonObject.get("xpath"));
					String idxpath=object2String(jsonObject.get("idxpath"));
					String ide=object2String(jsonObject.get("ide"));
					String robula=object2String(jsonObject.get("robula"));
					String montoto=object2String(jsonObject.get("montoto"));
					String xStr=object2String(jsonObject.get("x"));
					String yStr=object2String(jsonObject.get("y"));
					String widthStr=object2String(jsonObject.get("width"));
					String heightStr=object2String(jsonObject.get("height"));

					int x=string2Int(xStr);
					int y=string2Int(yStr);
					int width=string2Int(widthStr);
					int height=string2Int(heightStr);

					if(width>0 && height>0)
					{
						Locator locator=new Locator();

						locator.setLocationArea(new Rectangle(x, y, width, height));

						addMetadata(locator, "tag", tag);
						addMetadata(locator, "class", className);
						addMetadata(locator, "type", type);
						addMetadata(locator, "name", name);
						addMetadata(locator, "id", id);
						addMetadata(locator, "value", value);
						addMetadata(locator, "href", href);
						if(isValidText(text))
						{
							addMetadata(locator, "text", text);
						}
						addMetadata(locator, "placeholder", placeholder);
						addMetadata(locator, "title", title);
						addMetadata(locator, "alt", alt);
						addMetadata(locator, "xpath", xpath);
						addMetadata(locator, "idxpath", idxpath);
						addMetadata(locator, "x", xStr);
						addMetadata(locator, "y", yStr);
						addMetadata(locator, "height", heightStr);
						addMetadata(locator, "width", widthStr);
						int area = width * height;
						int shape = (width * 100) / height;
						addMetadata(locator, "area", ""+area);
 						addMetadata(locator, "shape", ""+shape);

						addMetadata(locator, "ide", ide);
						addMetadata(locator, "robula", robula);
						addMetadata(locator, "montoto", montoto);

						String visibleText=locator.getVisibleText();
						if(visibleText!=null)
						{
							locator.putMetadata("visible_text", visibleText);
						}
						String isButton=isButton(tag, type, className)?"yes":"no";;
						locator.putMetadata("is_button", isButton);
						
						locators.add(locator);
					}
				}

				if(locators.size()!=1)
				{
					return null;
				}
				return locators.get(0);
			}
			catch (Exception e)
			{
				return null;
			}
		}

		return null;
	}

	public Locator getXPathLocatorForElement(String elementXPath)
	{
		List<Locator> locators=new ArrayList<Locator>();
		
		if(webDriver!=null)
		{
			try
			{
				String javascript = loadTextFile("javascript.js");
				webDriver.manage().timeouts().setScriptTimeout(300, TimeUnit.SECONDS);
				JavascriptExecutor executor = (JavascriptExecutor) webDriver;
				Object object=executor.executeScript(javascript +
					"var result = []; " +
					"var all = []; " +
					"var element = locateElementByXPath(\""+elementXPath+"\"); " +
					"if (element!=null) all.push(element); " +
					"for (var i=0, max=all.length; i < max; i++) { " +
					"    result.push({'tag': all[i].tagName, 'class': all[i].className, 'type': all[i].type, 'name': all[i].name, 'id': all[i].id, 'value': all[i].value, 'href': all[i].href, 'text': all[i].textContent, 'placeholder': all[i].placeholder, 'title': all[i].title, 'alt': all[i].alt, 'x': getXPosition(all[i]), 'y': getYPosition(all[i]), 'width': getMaxWidth(all[i]), 'height': getMaxHeight(all[i]), 'children': all[i].children.length, 'xpath': getXPath(all[i]), 'idxpath': getIdXPath(all[i])}); " +
					"} " +
					" return JSON.stringify(result); ");		

				String json=object.toString();
				JSONParser parser = new JSONParser();
				JSONArray jsonArray = (JSONArray)parser.parse(json);

				if(jsonArray.size()<1)
				{
					return null;
				}
				
				for(int i=0; i<jsonArray.size(); i++)
				{
					JSONObject jsonObject=(JSONObject)jsonArray.get(i);

					String tag=object2String(jsonObject.get("tag"));
					if(tag!=null)
					{
						tag=tag.toLowerCase();
					}
					String className=object2String(jsonObject.get("class"));
					String type=object2String(jsonObject.get("type"));
					String name=object2String(jsonObject.get("name"));
					String id=object2String(jsonObject.get("id"));
					String value=object2String(jsonObject.get("value"));
					String href=object2String(jsonObject.get("href"));
					String text=object2String(jsonObject.get("text"));
					String placeholder=object2String(jsonObject.get("placeholder"));
					String title=object2String(jsonObject.get("title"));
					String alt=object2String(jsonObject.get("alt"));
					String xpath=object2String(jsonObject.get("xpath"));
					String idxpath=object2String(jsonObject.get("idxpath"));
					String xStr=object2String(jsonObject.get("x"));
					String yStr=object2String(jsonObject.get("y"));
					String widthStr=object2String(jsonObject.get("width"));
					String heightStr=object2String(jsonObject.get("height"));

					int x=string2Int(xStr);
					int y=string2Int(yStr);
					int width=string2Int(widthStr);
					int height=string2Int(heightStr);

					if(width>0 && height>0)
					{
						Locator locator=new Locator();

						locator.setLocationArea(new Rectangle(x, y, width, height));

						addMetadata(locator, "tag", tag);
						addMetadata(locator, "class", className);
						addMetadata(locator, "type", type);
						addMetadata(locator, "name", name);
						addMetadata(locator, "id", id);
						addMetadata(locator, "value", value);
						addMetadata(locator, "href", href);
						if(isValidText(text))
						{
							addMetadata(locator, "text", text);
						}
						addMetadata(locator, "placeholder", placeholder);
						addMetadata(locator, "title", title);
						addMetadata(locator, "alt", alt);
						addMetadata(locator, "xpath", xpath);
						addMetadata(locator, "idxpath", idxpath);
						addMetadata(locator, "x", xStr);
						addMetadata(locator, "y", yStr);
						addMetadata(locator, "height", heightStr);
						addMetadata(locator, "width", widthStr);

						int area = width * height;
						int shape = (width * 100) / height;
						addMetadata(locator, "area", ""+area);
 						addMetadata(locator, "shape", ""+shape);

						String visibleText=locator.getVisibleText();
						if(visibleText!=null)
						{
							locator.putMetadata("visible_text", visibleText);
						}
						String isButton=isButton(tag, type, className)?"yes":"no";;
						locator.putMetadata("is_button", isButton);
						
						locators.add(locator);
					}
				}

				if(locators.size()!=1)
				{
					return null;
				}
				return locators.get(0);
			}
			catch (Exception e)
			{
				return null;
			}
		}

		return null;
	}

	private void addMetadata(Locator locator, String key, String value)
	{
		if(value!=null && value.length()>0)
		{
			locator.putMetadata(key, value);
		}
	}

	private void addNeighborTexts(Locator locator, List<Locator> availableLocators)
	{
		if(locator.getLocationArea()==null)
		{
			return;
		}
		Rectangle r = locator.getLocationArea();
		if(r.height>100 || r.width > 600)
		{
			return;
		}
		Rectangle largerRectangle = new Rectangle(r.x-50, r.y-50, r.width+100, r.height+100);

		List<Locator> neighbors = new ArrayList<Locator>();
		for(Locator available:availableLocators)
		{
			if(locator!=available && available.getLocationArea()!=null)
			{
				Rectangle rect = available.getLocationArea();
				if(rect.getHeight()<=100 && largerRectangle.intersects(rect))
				{
			  	neighbors.add(available);
				}
			}
		}
		
		List<String> words = new ArrayList<String>();
		Properties wordHash = new Properties();
		for(Locator neighbor:neighbors)
		{
			String visibleText=neighbor.getVisibleText();
			if(visibleText != null)
			{
				String[] visibleWords = visibleText.split("\\s+");
				for(String visibleWord:visibleWords)
				{
					String visibleWordLower = visibleWord.toLowerCase();
					if(!wordHash.containsKey(visibleWordLower))
					{
						wordHash.put(visibleWordLower, true);
						words.add(visibleWordLower);
					}
				}
			}
		}
		
		StringBuffer wordString = new StringBuffer();
		for(String word:words)
		{
			if(wordString.length()>0)
			{
				wordString.append(" ");
			}
			wordString.append(word);
		}

		if(wordString.length()>0)
		{
			String text = wordString.toString();
			locator.putMetadata("neighbor_text", text);
		}
	}
	
	private boolean containsWord(String containsWord, String[] words)
	{
		for(String word:words)
		{
			if(containsWord.length() < word.length() && (word.startsWith(containsWord) || word.endsWith(containsWord)))
			{
				return true;
			}
			else if(word.length() < containsWord.length() && (containsWord.startsWith(word) || containsWord.endsWith(word)))
			{
				return true;
			}
			else if(containsWord.equals(word))
			{
				return true;
			}
		}
		return false;
	}

	private int compareNeighborText(String text1, String text2, int maxScore)
	{
		String[] words1 = text1.split("\\s+");
		String[] words2 = text2.split("\\s+");
		
		int existsCount = 0;
		int wordCount = Math.max(text1.length() - words1.length + 1, text2.length() - words2.length + 1);
		for(String word1:words1)
		{
			if(containsWord(word1, words2))
			{
				existsCount += word1.length();
			}
		}
		int score = Math.min((existsCount * maxScore) / wordCount, 100);
		return score;
	}

	private String object2String(Object o)
	{
		if(o==null)
		{
			return null;
		}
		if(o instanceof String)
		{
			String s=(String)o;
			return s.trim();
		}
		else if(o instanceof Integer)
		{
			Integer i=(Integer)o;
			return i.toString();
		}
		if(o instanceof Double)
		{
			Double d=(Double)o;
			int i=d.intValue();
			return ""+i;
		}
		else if(o instanceof Long)
		{
			Long l=(Long)o;
			return l.toString();
		}
		return null;
	}

	private int string2Int(String text)
	{
		try
		{
			return Integer.parseInt(text);
		}
		catch(Exception e)
		{
			return 0;
		}
	}

	private boolean isValidText(String text)
	{
		if(text==null)
		{
			return false;
		}
		String trimmedText=text.trim();
		if(trimmedText.length()<3 || trimmedText.length()>50)
		{
			// Too short or too long
			return false;
		}
		if(trimmedText.indexOf('\n')>=0)
		{
			// Contains newline
			return false;
		}
		if(trimmedText.indexOf('\t')>=0)
		{
			// Contains tab
			return false;
		}
		return true;
	}

	private boolean isButton(String tag, String type, String className)
	{
		if(tag==null)
		{
			return false;
		}
		if(tag.equalsIgnoreCase("a") && className!=null && className.indexOf("btn")>=0)
		{
			return true;
		}
		if(tag.equalsIgnoreCase("button"))
		{
			return true;
		}
		if(tag.equalsIgnoreCase("input") && ("button".equalsIgnoreCase(type) || "submit".equalsIgnoreCase(type) || "reset".equalsIgnoreCase(type)))
		{
			return true;
		}
		return false;
	}

	private List<String> readLines(File file)
	{
		List<String> lines=new ArrayList<String>();
		try
		{
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine())
			{
				String line=scanner.nextLine().trim();
				if(line.length()>0)
				{
					lines.add(line);
				}
			}
			scanner.close();
		}
		catch (Exception e)
		{
		}
		return lines;
	}

	private String loadTextFile(String filename)
	{
		File file=new File(filename);
		if(file.exists())
		{
			List<String> lines=readLines(file);
			StringBuffer buf=new StringBuffer();
			for(String line:lines)
			{
				buf.append(line);
				buf.append("\n");
			}
			return buf.toString();
		}
		return null;
	}

	/**
	 * Delay the thread a number of milliseconds
	 * @param milliseconds
	 */
	public void delay(int milliseconds)
	{
		try
		{
			Thread.sleep(milliseconds);
		}
		catch (InterruptedException e)
		{
		}
	}
}
