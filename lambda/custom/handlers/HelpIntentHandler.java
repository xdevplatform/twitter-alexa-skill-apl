package handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;

import java.util.Optional;

public class HelpIntentHandler implements IntentRequestHandler {
  @Override
  public boolean canHandle(HandlerInput handlerInput, IntentRequest intentRequest) {
    return intentRequest.getIntent().getName().equals("AMAZON.HelpIntent");
  }

  @Override
  public Optional<Response> handle(HandlerInput handlerInput, IntentRequest intentRequest) {
    final String speechText = "I can give you trends for a city or give you Tweets about a topic. What would you like?";
    return handlerInput.getResponseBuilder().withSpeech(speechText).withReprompt(speechText).build();
  }
}
