package eis.project.valiutukursai.Service;

import eis.project.valiutukursai.Entity.Rate;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

@Service
public class XMLService {

    public List<Rate> listOfRates(String URL) throws Exception {

        NodeList XMLRatesList = getXMLList(URL);
        List<Rate> ratesList = new ArrayList<>();

        for (int i = 0; i < XMLRatesList.getLength(); i++) {
            Node node = XMLRatesList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                Rate rate = new Rate(
                        element.getElementsByTagName("Dt").item(0).getTextContent(),
                        element.getElementsByTagName("Ccy").item(1).getTextContent(),
                        Double.parseDouble(element.getElementsByTagName("Amt").item(1).getTextContent()));
                ratesList.add(rate);
            }
        }
        return ratesList;
    }

    public List<String> listOfAvailableCurrencies(String URL) throws Exception {

        NodeList XMLCountriesList = getXMLList(URL);
        List<String> currenciesList = new ArrayList<>();

        for (int i = 0; i < XMLCountriesList.getLength(); i++) {
            Node node = XMLCountriesList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                currenciesList.add(element.getElementsByTagName("Ccy").item(1).getTextContent());
            }
        }
        return currenciesList;
    }

    private NodeList getXMLList(String URL) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(URL);

        document.getDocumentElement().normalize();

        NodeList XMLRatesList = document.getElementsByTagName("FxRate");
        return XMLRatesList;
    }
}