import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ReadXMLFile {

    private Automata automata;

    public ReadXMLFile(String uri) {

        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            DefaultHandler handler = new DefaultHandler() {

                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

                    if (qName.equalsIgnoreCase("automata")) {

                        automata = new Automata();
                        automata.setType(attributes.getValue("type"));
                    } else if (qName.equalsIgnoreCase("alphabets")) {

                        automata.setNumberOfAlphabets(Integer.parseInt(attributes.getValue("numberOfAlphabets")));
                    } else if (qName.equalsIgnoreCase("alphabet")) {

                        automata.addToLetters(attributes.getValue("letter").charAt(0));
                    } else if (qName.equalsIgnoreCase("states")) {

                        automata.setNumberOfStates(Integer.parseInt(attributes.getValue("numberOfStates")));
                    } else if (qName.equalsIgnoreCase("state")) {

                        automata.addToStates(attributes.getValue("name"),
                                Integer.parseInt(attributes.getValue("positionX")) + 1,
                                Integer.parseInt(attributes.getValue("positionY")) + 1);
                    } else if (qName.equalsIgnoreCase("initialState")) {

                        automata.setInitialState(attributes.getValue("name"));
                    } else if (qName.equalsIgnoreCase("finalStates")) {

                        automata.setNumberOfFinalStates(Integer.parseInt(attributes.getValue("numberOfFinalStates")));
                    } else if (qName.equalsIgnoreCase("finalState")) {

                        automata.addToFinalStates(attributes.getValue("name"));
                    } else if (qName.equalsIgnoreCase("transitions")) {

                        automata.setNumberOfTrans(Integer.parseInt(attributes.getValue("numberOfTrans")));
                    } else if (qName.equalsIgnoreCase("transition")) {

                        automata.addToTrans(attributes.getValue("name"),
                                attributes.getValue("source"),
                                attributes.getValue("destination"),
                                attributes.getValue("label"));
                    }
                }
            };

            saxParser.parse(uri, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }

        automata.makeThingsRight();
    }

    public Automata getAutomata() {
        return automata;
    }
}
