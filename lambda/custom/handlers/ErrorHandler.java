package handlers;

import com.amazon.ask.dispatcher.exception.ExceptionHandler;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;

import java.util.Optional;

public class ErrorHandler implements ExceptionHandler {

  @Override
  public boolean canHandle(HandlerInput handlerInput, Throwable throwable) {
    return true;
  }

  @Override
  public Optional<Response> handle(HandlerInput handlerInput, Throwable throwable) {
    final String speechText = "Sorry, I can't understand the command, please say it again";
    return handlerInput.getResponseBuilder()
        .withSpeech(speechText)
        .withReprompt(speechText)
        .build();
  }
}
