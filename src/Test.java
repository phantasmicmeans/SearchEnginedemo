import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

public class Test {

	static String dbnamestring;
	static String dbtitlestring;
	static String dbtextstring;
	static Environment myDbEnvironment;
	static Database myDatabase;
	static StringBuffer sb;
	static int DBCount = 0;
	static int InvertedIndex[];
	static int datanum = 0;
	static int jj = 0;
	static HashMap<String, Integer> InputHash;
	static HashMap<String, Integer> DocHash;

	static HashMap<String, Integer> InputHashSwing;
	static HashMap<String, Integer> DocHashSwing;

	static int finalDB[];
	static double CosineDB[];

	static int finalDBSwing[];
	static double CosineDBSwing[];

	static String guifinalstring;
	static Guisearcher a = new Guisearcher();

	public static void main(String[] args) {
		
		SetDB();
		InputHash = new HashMap<String, Integer>();
		Scanner sc= new Scanner(System.in);
		String input=sc.nextLine();
		InputHash = makeInputHash(input);
		a.result.setText(Start(InputHash));
		sc.close();
		

	}

	
	public static void SetDB() {

		try {

			EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setAllowCreate(true);
			myDbEnvironment = new Environment(new File("."), envConfig);

			DatabaseConfig dbConfig = new DatabaseConfig();
			dbConfig.setAllowCreate(true);
			myDatabase = myDbEnvironment.openDatabase(null, "TestDataBase", dbConfig);

			for (int k = 1; k < 31; k++) {
				if(k!=18)
				{
					String path = "src/" + String.valueOf(k) + ".xml";
				BufferedReader inb = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));

				String set = null;
				sb = new StringBuffer();

				set = inb.readLine();
				while ((set = inb.readLine()) != null) {// 한줄씩 읽어
					sb.append(set);
				}

				Parse(sb.toString().replaceAll("<td(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", ""));
				
				inb.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void CloseDB() {
		try {
			if (myDatabase != null) {
				myDatabase.close();
			}
			if (myDbEnvironment != null) {
				myDbEnvironment.close();
			}
		} catch (DatabaseException dbe) {
			// Exception handling
		}

	}

	public static void Parse(String sb) {

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document doc2 = dBuilder.parse(new InputSource(new StringReader(sb)));
			doc2.getDocumentElement().normalize();

			NodeList nList = doc2.getElementsByTagName("DOC"); // staff밑의 원소들

			datanum = nList.getLength();
			InvertedIndex = new int[datanum];

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					dbnamestring = getTagValue("DOCNAME", eElement);
					dbtitlestring = getTagValue("TITLE", eElement);
					dbtextstring = getTagValue("TEXT", eElement);

					InputDB(DBCount++, dbnamestring + dbtitlestring + "\n" + dbtextstring);

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag);
		Node nValue = (Node) nlList.item(0);
		String res = nValue.getTextContent();

		return res;
	}

	public static void InputDB(int DBCount, String data) {// 이건 DB에 넣는 함수
		try {

			DatabaseEntry theDBCount = new DatabaseEntry(Integer.toString(DBCount).getBytes("UTF-8"));
			DatabaseEntry theData = new DatabaseEntry(data.getBytes("UTF-8"));
			myDatabase.put(null, theDBCount, theData);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String SearchDB(int DBCount) {// DB에서 검색하는 함수

		String foundData = null;

		try {
			DatabaseEntry theKey = new DatabaseEntry(Integer.toString(DBCount).getBytes("UTF-8"));
			DatabaseEntry theData = new DatabaseEntry();

			if (myDatabase.get(null, theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

				byte[] retData = theData.getData();
				foundData = new String(retData, "UTF-8");
			} else {
				System.out.println("No record found with the key '" + DBCount + "'.");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return foundData;
	}

	public static HashMap<String, Integer> makeDocHash(String doc) {

		int intdoc[] = { 0 };
		String finaldoc[];

		intdoc = new int[doc.length()];
		String newstrdoc[] = new String[doc.length()];

		String docs[] = new String[doc.length()];
		docs = doc.split(" ");

		int count = 0;

		for (int i = 0; i < docs.length; i++) {

			if (docs[i].length() == 1) {
				newstrdoc[count] = docs[i];
				intdoc[count]++;
				count++;
			} else {
				for (int j = 0; j < docs[i].length() - 1; j++) {

					newstrdoc[count] = docs[i].substring(j, j + 2);
					intdoc[count]++;
					count++;

				}
			}

		} // 문서 2글자로 쪼갬

		finaldoc = new String[count];

		for (int j = 0; j < count; j++) {
			finaldoc[j] = newstrdoc[j];

		}

		for (int i = 0; i < count; i++) {
			for (int j = 0; j < i; j++) {
				if (finaldoc[j].equals(finaldoc[i])) {
					intdoc[j]++;
					for (int k = i; k < count - 1; k++) {
						finaldoc[k] = finaldoc[k + 1];
						intdoc[k] = intdoc[k + 1];

					}
					count--;
				}
			}
		}

		HashMap<String, Integer> dochash = new HashMap<String, Integer>();

		for (int i = 0; i < count; i++) {

			dochash.put(finaldoc[i], intdoc[i]);

		}

		return dochash;
	}

	public static HashMap<String, Integer> makeInputHash(String input) {

		String ipt[] = new String[input.length()];
		int intinput[] = new int[input.length()];

		String inputsplit[] = new String[input.length()];
		inputsplit = input.split(" ");
		int count = 0; // 입력에서 단어의 개수

		for (int i = 0; i < inputsplit.length; i++) {
			if (inputsplit[i].length() == 1) {
				ipt[count] = inputsplit[i];
				intinput[count]++;

				count++;
			} else {
				for (int j = 0; j < inputsplit[i].length() - 1; j++) {
					ipt[count] = inputsplit[i].substring(j, j + 2);
					intinput[count]++;
					count++;
				}
			}
		}

		String finalipt[] = new String[count];
		for (int i = 0; i < count; i++) {
			finalipt[i] = ipt[i];
		}

		HashMap<String, Integer> ipthash = new HashMap<String, Integer>();

		for (int i = 0; i < count; i++) {
			ipthash.put(finalipt[i], intinput[i]);

		}

		return ipthash;

	}

	public static int innerProduct(HashMap<String, Integer> InputHash, HashMap<String, Integer> DocHash) {
		// 내적 구하기.
		Iterator<String> InputkeySet = InputHash.keySet().iterator();

		int inner = 0;

		while (InputkeySet.hasNext()) {
			String Iptkey = InputkeySet.next();
			Iterator<String> DockeySet = DocHash.keySet().iterator();

			while (DockeySet.hasNext()) {
				String Dockey = DockeySet.next();

				if (Iptkey.equals(Dockey)) {
					inner += InputHash.get(Iptkey) * DocHash.get(Dockey);
					break;
				}
			}
		}

		return inner;

	}

	public static double makeLength(HashMap<String, Integer> H) // 길이
	{
		Iterator<String> InputkeySet = H.keySet().iterator();
		double res = 0;

		while (InputkeySet.hasNext()) {
			String key = InputkeySet.next();
			res += Math.pow(H.get(key), 2);
		}

		res = Double.parseDouble(String.format("%.3f", Math.sqrt(res)));

		return res;
	}

	public static double CosineSimilarity(HashMap<String, Integer> InputHash, HashMap<String, Integer> DocHash) {

		double cosineSimresult = innerProduct(InputHash, DocHash) / (makeLength(InputHash) * makeLength(DocHash));
		System.out.println("makeLength(InputHash) : " + makeLength(InputHash));
		System.out.println("makeLength(DocHash) : " + makeLength(DocHash));
		return Double.parseDouble(String.format("%.5f", cosineSimresult));

	}

	public static String SearchTopDoc(int num_rets) {
		guifinalstring = null;

		int count = 1;
		int guicount = 1;
		String res = "";
		System.out.println("총 검색 결과 수 : " + num_rets);
		a.numretlb.setText("총 검색 결과 수 : " + num_rets);
		System.out.println("------------------검색 결과 top 순위-------------------");
		for (int i = 0; i < num_rets; i++) {
			String DOCNAME = "";
			String TITLE = "";
			String db = SearchDB(finalDB[i]);

			for (int j = 0; j < 13; j++) {
				DOCNAME += db.charAt(j);
			}
			for (int j = 13; j < 40; j++) {
				if (db.charAt(j) == '\n')
					break;

				TITLE += db.charAt(j);
			}

			System.out.println((count++ + " : " + "DOCNAME : " + DOCNAME + ", " + "Cosine Score: " + CosineDB[i]
					+ ", TITLE : " + TITLE));
			res += (guicount++ + " : " + "DOCNAME : " + DOCNAME + ", " + "Cosine Score: " + CosineDB[i] + ", TITLE : "
					+ TITLE) + "\n";
			
		}

		return res;

	}
	
	public static String Start(HashMap<String, Integer> IptHash) {

		int num_rets = 0;
		finalDB = new int[DBCount];
		CosineDB = new double[DBCount];

		for (int i = 0; i < DBCount; i++) {
			DocHash = makeDocHash(SearchDB(i));

			double cosineres = CosineSimilarity(IptHash, DocHash);
			if (cosineres != 0.0) {
				finalDB[num_rets] = i;
				CosineDB[num_rets] = cosineres;
				num_rets++;
			}

			System.out.println("Cosine : " + cosineres);
		}

		sort(finalDB, CosineDB, num_rets);
		return SearchTopDoc(num_rets);

	}

	public static void sort(int finalDB[], double CosineDB[], int num_rets) {
		double tempd = 0;
		int tempi = 0;

		for (int i = 0; i < num_rets; i++) {
			for (int j = i + 1; j < num_rets; j++) {
				if (CosineDB[i] < CosineDB[j]) {
					tempd = CosineDB[j];
					CosineDB[j] = CosineDB[i];
					CosineDB[i] = tempd;

					tempi = finalDB[j];
					finalDB[j] = finalDB[i];
					finalDB[i] = tempi;
				}

			}
		}
	}
}
