# Build An Alexa Skill for Twitter - Tweet Buddy

A sample Alexa skill that brings the Twitter experience to Alexa. For multimodal devices, you can see Tweets about a certain topic or trends for a city.

# Demo Features

This skill demonstrates:

- Use of Twitter APIs - search API and trends API
- Alexa Presentation Language (APL) pager functionality with speak item commands

# AWS Lambda Setup

1. Go to the [AWS Console](http://console.aws.amazon.com/) and create a Lambda function.
2. Select author from scratch and give your function a name (e.g. twitter-skill) and select Java 8 as the runtime.
3. Select appropriate role and click create function.
4. Build a jar file to upload it into the lambda function:
    - Make sure that you replace the Twitter API Keys and tokens in [TwitterService.java](https://github.com/twitterdev/twitter-alexa-skill-apl/tree/master/lambda/custom/util/TwitterService.java) file
    - Using maven: go to the directory containing pom.xml, and run 'mvn assembly:assembly -DdescriptorId=jar-with-dependencies package'. This will generate a zip file named "tweet-buddy-1.0-jar-with-dependencies.jar" in the target directory.
5. For Code entry type, select "Upload a .ZIP file" and then upload the jar file created in the previous step from the build directory to Lambda.
6. Set the Handler as TweetBuddyStreamHandler
7. Increase the Timeout to 30 seconds and make sure memory is set to at least 512 MB under Basic Settings.
8. Click add trigger, and select Alexa skills kit. You will add your skill id (from the alexa skill setup section) here.
9. Save your settings and copy the ARN from the top right to be used later in the Alexa Skill Setup.

# Alexa Skill Setup

1. Go to the [Alexa Console](https://developer.amazon.com) and click Create Skill.
2. Set "Tweet Buddy" as the skill name, Custom for the model and 'provision your own' for hosting method. Next, select Start from Scratch for the template.
3. In the Endpoints tab, select AWS Lambda ARN and paste the arn for your lambda function here.
4. From the Interfaces tab, select Alexa Presentation Language and check the Hub Landscape, Small.
5. Build the Interaction Model for your skill:
    - On the left hand navigation panel, select the JSON Editor tab under Interaction Model. In the textfield provided, replace any existing code with the code provided in the [Interaction Model](https://github.com/twitterdev/twitter-alexa-skill-apl/blob/master/models/en-US.json).  Click Save Model.
    - Click "Build Model".
6. Make sure you copy the Skill Id from your developer console and use it as the trigger for your lambda function.

# Examples
    User: "Alexa, open Tweet Buddy"
    User: "What's Trending in Seattle"
    User: "Give me Tweets about Lamar Jackson"
