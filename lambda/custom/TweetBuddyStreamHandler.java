import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;

import handlers.LaunchHandler;
import handlers.CancelAndStopIntentHandler;
import handlers.FallbackIntentHandler;
import handlers.HelpIntentHandler;
import handlers.SessionEndedIntentHandler;
import handlers.TrendHandler;
import handlers.TweetHandler;
import handlers.ErrorHandler;

public class TweetBuddyStreamHandler extends SkillStreamHandler {

  private static Skill getSkill() {
    return Skills.standard()
        .addRequestHandlers(
            new LaunchHandler(),
            new CancelAndStopIntentHandler(),
            new FallbackIntentHandler(),
            new HelpIntentHandler(),
            new SessionEndedIntentHandler(),
            new TrendHandler(),
            new TweetHandler())
        .addExceptionHandler(new ErrorHandler())
        .build();
  }

  public TweetBuddyStreamHandler() {
    super(getSkill());
  }
}
