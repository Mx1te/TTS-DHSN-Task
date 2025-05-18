# TTS-DHSN-Task
Text-to-Speech integration using FreeTTs 


TODO-List:

~ install and set up FreeTTS Libary
~ test basic functionality
    -> how to change settings
    -> how to get/set data
    -> what kind of data is needed: json?, simple string?
~ create basic TTS-function

u need to manually install following libs with maven:
- cmulex
- cmutimelex
- en_us_kal.jar
- freetts.jar

with 
mvn install:install-file \
  -Dfile=lib/freetts.jar \
  -DgroupId=com.sun.speech \
  -DartifactId=freetts \
  -Dversion=1.2.2 \
  -Dpackaging=jar

then

mvn clean install

then

mvn spring-boot:run -e


Info and Doc's:

https://freetts.sourceforge.io/docs/index.html#how_app

https://www.youtube.com/watch?v=8XaAEPOvn6k