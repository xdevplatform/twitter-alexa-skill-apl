package handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.Command;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.SpeakItemCommand;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.SetPageCommand;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.SequentialCommand;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.Position;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.RenderDocumentDirective;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.ExecuteCommandsDirective;
import com.amazon.ask.request.RequestHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import twitter4j.Status;
import util.SkillData;
import util.TwitterService;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class TweetHandler implements IntentRequestHandler {

  private TwitterService twitterService = new TwitterService();

  @Override
  public boolean canHandle(HandlerInput handlerInput, IntentRequest intentRequest) {
    return intentRequest.getIntent().getName().equals("TweetIntent");
  }

  @Override
  public Optional<Response> handle(HandlerInput handlerInput, IntentRequest intentRequest) {
    final RequestHelper requestHelper = RequestHelper.forHandlerInput(handlerInput);
    final String searchTerm = requestHelper.getSlotValue("SearchTerm").get();

    final List<Status> statuses = SkillData.getFilteredStatuses(twitterService.getTweetsBySearchTerm(searchTerm));
    if (statuses.size() < 3) {
      return getNotEnoughTweetsResponse(handlerInput, searchTerm);
    }
    final String tweetsAsSpeechText = SkillData.getTweetsAsSpeechText(statuses);
    final String speechText = String.format("Here are some Tweets about %s.", searchTerm);

    if (SkillData.supportsAPL(handlerInput)) {

      String content = SkillData.getAplDocforTweets();

      Map<String, Object> document = new Gson().fromJson(content, new TypeToken<HashMap<String, Object>>() {
      }.getType());

      Map<String, Object> dataSource = new Gson().fromJson(SkillData.getDataSourceForTweets(statuses),
          new TypeToken<HashMap<String, Object>>() {
          }.getType());

      List<Command> commandList = new ArrayList<>();
      commandList.add(SpeakItemCommand.builder().withComponentId("tweet1").withDelay(500).build());
      commandList.add(
          SetPageCommand.builder().withComponentId("pagerId").withDelay(100).withPosition(Position.RELATIVE).build());
      commandList.add(SpeakItemCommand.builder().withComponentId("tweet2").withDelay(500).build());
      commandList.add(
          SetPageCommand.builder().withComponentId("pagerId").withDelay(100).withPosition(Position.RELATIVE).build());
      commandList.add(SpeakItemCommand.builder().withComponentId("tweet3").withDelay(500).build());

      SequentialCommand command = SequentialCommand.builder().withCommands(commandList).withDelay(300).build();

      return handlerInput.getResponseBuilder().withSpeech(speechText)
          .addDirective(RenderDocumentDirective.builder().withDocument(document).withDatasources(dataSource)
              .withToken("pager").build())
          .addDirective(ExecuteCommandsDirective.builder().addCommandsItem(command).withToken("pager").build()).build();
    }

    return handlerInput.getResponseBuilder().withSpeech(String.format("%s %s", speechText, tweetsAsSpeechText)).build();
  }

  private Optional<Response> getNotEnoughTweetsResponse(HandlerInput handlerInput, String topic) {
    String speechText = String.format(
        "There aren't enough Tweets about %s. " + "Please trying asking for Tweets about a different topic", topic);
    return handlerInput.getResponseBuilder().withSpeech(speechText).build();
  }

}
