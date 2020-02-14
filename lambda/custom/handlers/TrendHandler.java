package handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.RenderDocumentDirective;
import com.amazon.ask.request.RequestHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import twitter4j.Trends;
import util.SkillData;
import util.TwitterService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TrendHandler implements IntentRequestHandler {

  private TwitterService twitterService = new TwitterService();

  @Override
  public boolean canHandle(HandlerInput handlerInput, IntentRequest intentRequest) {
    return intentRequest.getIntent().getName().equals("TrendIntent");
  }

  @Override
  public Optional<Response> handle(HandlerInput handlerInput, IntentRequest intentRequest) {
    final RequestHelper requestHelper = RequestHelper.forHandlerInput(handlerInput);
    final String cityName = requestHelper.getSlotValue("City").get();
    final Trends trends = twitterService.getTrends(cityName);
    final String trendsAsString = SkillData.getTrendsAsString(twitterService.getTrends(cityName));
    final String speechText = String.format("Here are some top trends for %s: %s", cityName, trendsAsString);

    //Supports display
    if (SkillData.supportsAPL(handlerInput)) {

      String content = SkillData.getAplDocForTrends();

      Map<String, Object> document = new Gson().fromJson(content,
          new TypeToken<HashMap<String, Object>>() {}.getType());

      Map<String, Object> dataSource = new Gson().fromJson(SkillData.getDataSourceForTrends(cityName, SkillData.getListOfRandomTrends(trends)),
          new TypeToken<HashMap<String, Object>>() {}.getType());

      return handlerInput.getResponseBuilder()
          .withSpeech(speechText)
          .addDirective(RenderDocumentDirective.builder()
              .withDocument(document)
              .withDatasources(dataSource)
              .build())
          .build();
    }

    //Headless device
    return handlerInput.getResponseBuilder()
        .withSpeech(speechText)
        .build();
  }

}
