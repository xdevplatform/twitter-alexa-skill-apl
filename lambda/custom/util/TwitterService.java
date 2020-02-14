package util;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.Trends;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

public class TwitterService {
  //FIXME replace the keys below
  private static final String API_KEY = "";
  private static final String API_SECRET = "";
  private static final String ACCESS_TOKEN = "";
  private static final String ACCESS_TOKEN_SECRET = "";
  private Twitter twitter;

  public TwitterService() {
    twitter = new TwitterFactory(new ConfigurationBuilder()
        .setOAuthConsumerKey(API_KEY)
        .setOAuthConsumerSecret(API_SECRET)
        .setOAuthAccessToken(ACCESS_TOKEN)
        .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET)
        .setTweetModeExtended(true)
        .build())
        .getInstance();
  }

  /*
   * This method returns Trends for a given location
   * */
  public Trends getTrends(String location) {
    Trends trends = null;
    try {
      int woeid = SkillData.LOCATION_MAP.get(location);
      trends = twitter.getPlaceTrends(woeid);
    } catch (TwitterException e) {
      e.printStackTrace();
    }
    return trends;
  }

  /*
   * This method returns list of Tweets based on a search term
   * */
  public List<Status> getTweetsBySearchTerm(String searchTerm) {
    Query query = new Query(String.format("-filter:retweets -filter:images %s", searchTerm));
    query.count(100);
    query.resultType(Query.POPULAR);
    QueryResult result = null;
    try {
      result = twitter.search(query);
    } catch (TwitterException e) {
      e.printStackTrace();
    }
    return result.getTweets();
  }

}
