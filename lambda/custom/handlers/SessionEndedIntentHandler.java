package handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.SessionEndedRequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.SessionEndedRequest;

import java.util.Optional;

public class SessionEndedIntentHandler implements SessionEndedRequestHandler {
  @Override
  public boolean canHandle(HandlerInput handlerInput, SessionEndedRequest sessionEndedRequest) {
    return true;
  }

  @Override
  public Optional<Response> handle(HandlerInput handlerInput, SessionEndedRequest sessionEndedRequest) {
    return handlerInput.getResponseBuilder()
        .build();
  }
}
