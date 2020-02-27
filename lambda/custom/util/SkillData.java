package util;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.SupportedInterfaces;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.AlexaPresentationAplInterface;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.commons.io.FileUtils;

import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class SkillData {

  public static final Map<String, Integer> LOCATION_MAP = new HashMap<String, Integer>() {
    {
      // FIXME This is just a small list of cities and WOEIDs.
      // Full list of available WOEIDs can be queried here
      // https://developer.twitter.com/en/docs/trends/locations-with-trending-topics/api-reference/get-trends-available
      put("Seattle", 2490383);
      put("Austin", 2357536);
      put("Chicago", 2379574);
      put("Houston", 2424766);
      put("Orlando", 2466256);
      put("New York", 2459115);
      put("Los Angeles", 2442047);
      put("San Francisco", 2487956);
      put("Denver", 2391279);
      put("London", 44418);
    }
  };

  public static String getTrendsAsString(Trends trends) {
    int i = 0;
    StringBuilder sb = new StringBuilder();
    for (Trend trend : trends.getTrends()) {
      if (i < 5) {
        sb.append(String.format("%s,", trend.getName().replace("#", "")));
        i++;
      }
    }
    return sb.toString();
  }

  public static List<String> getListOfRandomTrends(Trends trends) {
    int i = 0;
    List<String> trendList = new ArrayList<>();
    for (Trend trend : trends.getTrends()) {
      if (i < 5) {
        trendList.add(trend.getName().replace("#", ""));
        i++;
      }
    }
    return trendList;
  }

  public static String getAplDocForTrends() {
    String content = "";
    try {
      content = SkillData.getFileContentAsString("trends.json");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return content;
  }

  public static String getAplDocforTweets() {
    String result;
    try {
      result = SkillData.getFileContentAsString("tweets.json");
    } catch (IOException e) {
      e.printStackTrace();
      throw new AskSdkException("Unable to read or deserialize template data", e);
    }
    return result;
  }

  /*
   * Helper method that checks if the device supports display capabilities
   */
  public static boolean supportsAPL(HandlerInput input) {
    SupportedInterfaces supportedInterfaces = input.getRequestEnvelope().getContext().getSystem().getDevice()
        .getSupportedInterfaces();
    AlexaPresentationAplInterface alexaPresentationAplInterface = supportedInterfaces.getAlexaPresentationAPL();
    return alexaPresentationAplInterface != null;
  }

  public static String getFileContentAsString(String fileName) throws IOException {
    File file = new File(fileName);
    return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
  }

  /*
   * Excludes tweets text that include http in the text to make it cleaner for
   * Alexa to read
   */
  public static List<Status> getFilteredStatuses(List<Status> statues) {
    List<Status> statusList = new ArrayList<>();
    for (Status status : statues) {
      if (!status.getText().contains("http")) {
        statusList.add(status);
      }
    }
    return statusList;
  }

  public static String getTweetsAsSpeechText(List<Status> statuses) {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (Status status : statuses) {
      if (i < 5) {
        if (null != status.getUser() && null != status.getUser().getLocation()
            && !status.getUser().getLocation().equals("")) {
          sb.append(String.format("From %s of %s. %s. ", getCleanedStringForSpeech(status.getUser().getName()),
              getCleanedStringForSpeech(status.getUser().getLocation()), getCleanedStringForSpeech(status.getText())));
        } else {
          sb.append(String.format("From %s. %s. ", getCleanedStringForSpeech(status.getUser().getName()),
              getCleanedStringForSpeech(status.getText())));
        }
        i++;
      }
    }
    return sb.toString();
  }

  /*
   * Helper function that checks if a users location is present to include in
   * Alexa's speech
   */
  private static String getTweetForSpeechText(Status status) {
    if (null != status.getUser() && null != status.getUser().getLocation()
        && !status.getUser().getLocation().equals("")) {
      return String.format("From %s of %s. %s. ", getCleanedStringForSpeech(status.getUser().getName()),
          getCleanedStringForSpeech(status.getUser().getLocation()), getCleanedStringForSpeech(status.getText()));
    } else {
      return String.format("From %s. %s. ", getCleanedStringForSpeech(status.getUser().getName()),
          getCleanedStringForSpeech(status.getText()));
    }
  }

  /*
   * Helper function that removes emojis, whitespace and maintains quotes so Alexa
   * can pronounce just the strings
   */
  private static String getCleanedStringForSpeech(String text) {
    String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
    return text.replace("&", "and").replace("#", "").replaceAll("\"", "\\\\\"").replaceAll("\\s{2,}", " ")
        .replaceAll(characterFilter, "").trim();
  }

  /*
   * Helper function that replaces large emptyspace with a single line break
   */
  private static String getCleanedTweetText(Status tweet) {
    return tweet.getText().replaceAll("\"", "\\\\\"").replaceAll("\\s{2,}", "\n").trim();
  }

  public static JsonObject getDataSourceForTweets(List<Status> tweets) {
    JsonObject object = new JsonObject();
    JsonObject firstTweet = new JsonObject();
    firstTweet.addProperty("text", tweets.get(0).getText());
    firstTweet.addProperty("name", String.format("<b>%s</b>", tweets.get(0).getUser().getName()));
    firstTweet.addProperty("handle", String.format("@%s", tweets.get(0).getUser().getScreenName()));
    firstTweet.addProperty("imageUrl", tweets.get(0).getUser().getProfileImageURLHttps());
    firstTweet.add("properties", getTweetSsmlProperty(getTweetForSpeechText(tweets.get(0))));
    firstTweet.add("transformers", getTransformers("firstTweetAsSpeech"));
    object.add("firstTweet", firstTweet);

    JsonObject secondTweet = new JsonObject();
    object.add("secondTweet", secondTweet);
    secondTweet.addProperty("text", tweets.get(1).getText());
    secondTweet.addProperty("name", String.format("<b>%s</b>", tweets.get(1).getUser().getName()));
    secondTweet.addProperty("handle", String.format("@%s", tweets.get(1).getUser().getScreenName()));
    secondTweet.addProperty("imageUrl", tweets.get(1).getUser().getProfileImageURLHttps());
    secondTweet.add("properties", getTweetSsmlProperty(getTweetForSpeechText(tweets.get(1))));
    secondTweet.add("transformers", getTransformers("secondTweetAsSpeech"));
    object.add("secondTweet", secondTweet);

    JsonObject thirdTweet = new JsonObject();
    object.add("thirdTweet", thirdTweet);
    thirdTweet.addProperty("text", tweets.get(2).getText());
    thirdTweet.addProperty("name", String.format("<b>%s</b>", tweets.get(2).getUser().getName()));
    thirdTweet.addProperty("handle", String.format("@%s", tweets.get(2).getUser().getScreenName()));
    thirdTweet.addProperty("imageUrl", tweets.get(2).getUser().getProfileImageURLHttps());
    thirdTweet.add("properties", getTweetSsmlProperty(getTweetForSpeechText(tweets.get(2))));
    thirdTweet.add("transformers", getTransformers("thirdTweetAsSpeech"));
    object.add("thirdTweet", thirdTweet);

    return object;
  }

  private static JsonObject getTweetSsmlProperty(String text) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("tweetSsml", String.format("<speak>%s</speak>", text));
    return jsonObject;
  }

  private static JsonArray getTransformers(String label) {
    JsonArray array = new JsonArray();
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("inputPath", "tweetSsml");
    jsonObject.addProperty("outputName", label);
    jsonObject.addProperty("transformer", "ssmlToSpeech");
    array.add(jsonObject);
    return array;
  }

  public static JsonObject getDataSourceForTrends(String location, List<String> trends) {
    JsonObject jsonObject = new JsonObject();
    JsonObject data = new JsonObject();
    data.addProperty("location", String.format("<b>Top trends for %s</b>", location));
    data.addProperty("firstTrend", String.format("1. #%s", trends.get(0)));
    data.addProperty("secondTrend", String.format("2. #%s", trends.get(1)));
    data.addProperty("thirdTrend", String.format("3. #%s", trends.get(2)));
    data.addProperty("fourthTrend", String.format("4. #%s", trends.get(3)));
    data.addProperty("fifthTrend", String.format("5. #%s", trends.get(4)));
    jsonObject.add("data", data);
    return jsonObject;
  }
}
