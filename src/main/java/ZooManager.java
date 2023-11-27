import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.*;
import java.util.Scanner;

public class ZooManager {

    private static final String FILENAME = "zoo.xml";
    //obtiene un analizador que produce árboles de objetos DOM a partir de documentos XML.
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    //Con esta clase, se obtiene un documento XML.
    private static DocumentBuilder builder;
    //Clase para obtener todo el xml
    private static Document document;
    //Clase para obtener los elementos
    private static Element rootElement;

    public static void main(String[] args) {
        try {
            // Inicializar el documento.
            builder = factory.newDocumentBuilder();
            File file = new File(FILENAME);
            //Crea un nuevo documento llamado Zoo si el fichero no existe.
            if (!file.exists()) {
                document = builder.newDocument();
                rootElement = document.createElement("Zoo");
                document.appendChild(rootElement);
            } else {
                document = builder.parse(file);
                rootElement = document.getDocumentElement();
            }
            //Saldrá el menú hasta que el usuario introduzca 5.
            boolean exit = false;
            Scanner scanner = new Scanner(System.in);
            while (!exit) {
                System.out.println("\nMenu:");
                System.out.println("1.- Visualizar todos los felinos");
                System.out.println("2.- Añadir nuevos felinos");
                System.out.println("3.- Mostrar un felino");
                System.out.println("4.- Actualizar peso del felino");
                System.out.println("5.- Salir del programa");
                System.out.print("Ingrese su opción: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        showAllFelines();
                        break;
                    case 2:
                        addNewFeline();
                        break;
                    case 3:
                        showAllFelinesByContinent();
                        break;
                    case 4:
                        createModifiedXml();
                        break;
                    case 5:
                        exit = true;
                        break;
                    default:
                        System.out.println("Opción no válida. Inténtelo de nuevo.");
                        break;
                }
            }

            // Fuerzo a que el xml no esté escrito en una sola línea.
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(FILENAME));
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * showAllFelines: Este método muestra todos los datos de todos los felinos que hay en el xml.
     */
    private static void showAllFelines() {
        NodeList felinos = rootElement.getElementsByTagName("Felino");
        System.out.println("\nTodos los felinos en el zoo:");
        for (int i = 0; i < felinos.getLength(); i++) {
            Element felino = (Element) felinos.item(i);
            String nombre = felino.getElementsByTagName("Nombre").item(0).getTextContent();
            String origen = felino.getElementsByTagName("Origen").item(0).getTextContent();
            String peso = felino.getElementsByTagName("Peso").item(0).getTextContent();
            System.out.println("Nombre: " + nombre + ", Origen: " + origen + ", Peso: " + peso);
        }
    }

    /**
     * addNewFeline: Este método añade un felino con los datos introducidos por pantalla de nombre, origen, peso hasta que el usuario escriba n y le de a enter.
     */
    private static void addNewFeline() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            //Crea el elemento felino
            Element felino = document.createElement("Felino");

            System.out.print("Ingrese el nombre del nuevo felino: ");
            String nombre = scanner.nextLine();
            //Crea el elemento nombre
            Element nombreElement = document.createElement("Nombre");
            nombreElement.setTextContent(nombre);
            felino.appendChild(nombreElement);

            System.out.print("Ingrese el origen del nuevo felino: ");
            String origen = scanner.nextLine();
            //Crea el elemento origen
            Element origenElement = document.createElement("Origen");
            origenElement.setTextContent(origen);
            felino.appendChild(origenElement);

            System.out.print("Ingrese el peso del nuevo felino: ");
            String peso = scanner.nextLine();
            //Crea el elemento Peso
            Element pesoElement = document.createElement("Peso");
            pesoElement.setTextContent(peso);
            felino.appendChild(pesoElement);

            rootElement.appendChild(felino);
            System.out.println("Nuevo felino agregado al zoo.");

            System.out.print("¿Quieres añadir otro felino? (Ingrese 'n' para salir): ");
            String respuesta = scanner.nextLine();
            //Sale del bucle hasta que el usuario escriba n y le de enter
            if (respuesta.equalsIgnoreCase("n")) {
                break; // Exit the loop if the user enters 'n'
            }
        }
    }


    /**
     * showAllFelinesByContinen: Esta opción te preguntará “Elige continente:” y te mostrará únicamente los felinos del continente elegido.
     */
    private static void showAllFelinesByContinent() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Elige continente: ");
        String chosenContinent = scanner.nextLine();
        //Obtiene la lista del elemento Felino
        NodeList felinos = rootElement.getElementsByTagName("Felino");
        System.out.println("\nFelinos en el continente " + chosenContinent + ":");
        //Obtiene todos los felinos que haya en el xml
        for (int i = 0; i < felinos.getLength(); i++) {
            Element felino = (Element) felinos.item(i);
            //Obtiene el elemento Origen
            String origen = felino.getElementsByTagName("Origen").item(0).getTextContent();
            //Ignorar los casos en el que origen y chosenContinent sean iguales
            if (origen.equalsIgnoreCase(chosenContinent)) {
                String nombre = felino.getElementsByTagName("Nombre").item(0).getTextContent();
                String peso = felino.getElementsByTagName("Peso").item(0).getTextContent();
                System.out.println("Nombre: " + nombre + ", Origen: " + origen + ", Peso: " + peso);
            }
        }

    }

    /**
     * createModifiedXml: Este método es donde se creará el Zoo2.xml restándole 5 al peso de los felinos
     */
    private static void createModifiedXml() {
        try {
            // Clona el fichero existente a modificar sin modificar el original
            Document modifiedDocument = (Document) document.cloneNode(true);

            // Obtiene todos los elementos de Felino
            NodeList felinos = modifiedDocument.getElementsByTagName("Felino");

            // A todos los felinos se le resta 5 a su peso
            for (int i = 0; i < felinos.getLength(); i++) {
                Element felino = (Element) felinos.item(i);
                int peso = Integer.parseInt(felino.getElementsByTagName("Peso").item(0).getTextContent());
                int nuevoPeso = peso - 5;
                felino.getElementsByTagName("Peso").item(0).setTextContent(String.valueOf(nuevoPeso));
            }

            //Muestra el contenido modificado en la consola
            showAllFelines2(modifiedDocument);

            // Escribe los datos cambiados en un nuevo fichero llamado Zoo2.xml
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(modifiedDocument);
            StreamResult result = new StreamResult(new File("Zoo2.xml"));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// ...

    /**
     * showAllFelines2: Este método sirve para mostrar por pantalla los datos de los felinos con el peso actual y el que tendrán en el Zoo2.xml
     *
     * @param doc
     */
    private static void showAllFelines2(Document doc) {
        //Obtiene la lista de felinos
        NodeList felinos = doc.getElementsByTagName("Felino");
        System.out.println("\nTodos los felinos en el zoo:");
        //Obtiene el nombre, el origen y el peso del documento
        for (int i = 0; i < felinos.getLength(); i++) {
            Element felino = (Element) felinos.item(i);
            String nombre = felino.getElementsByTagName("Nombre").item(0).getTextContent();
            String origen = felino.getElementsByTagName("Origen").item(0).getTextContent();
            String peso = felino.getElementsByTagName("Peso").item(0).getTextContent();

            // Muestra el peso que va a tener en el Zoo2.xml (restándolo 5 al peso actual)
            int pesoOriginal = Integer.parseInt(peso);
            int nuevoPeso = pesoOriginal - 5;
            //Muestra por pantalla tanto el peso original como el nuevo peso además del nombre y origen del felino
            System.out.println("Nombre: " + nombre + ", Origen: " + origen + ", Peso Original: " + pesoOriginal + " - Nuevo Peso: " + nuevoPeso);
        }
    }
}
