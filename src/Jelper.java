import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Jelper {

	private static StringBuilder gendCode = new StringBuilder();
	private static StringBuilder gendJdbcCode = new StringBuilder();
	// Match Levels
	public static final int PERFECT_MATCH = 1;
	public static final int IGNORE_CASE_IND_WORDS = 2;
	public static final int TRY_MATCH_SHORTENED = 3;
	public static final int TRY_MATCH_SHORTENED_SHUFFLED = 4;
	public static final int MATCH_REMAINING_TYPE = 5;
	// Punctuations
	protected static final String PERIOD = ".";
	protected static final String OPEN_PARAN = "(";
	protected static final String CLOSED_PARAN = ")";
	protected static final String PAIR_PARAN = "()";
	protected static final String SEMI_COLON = ";";
	protected static final String NEW_LINE = "\n";
	protected static final char DOUBLE_QUOTE='"';
	// SET and GET
	protected static final String SET = "SET";
	protected static final String GET = "GET";


	protected static final Map<Class, Class> compatTypes = new HashMap<Class, Class>();
	private static int matchLevel = 3;

	static{
		compatTypes.put(int.class, BigDecimal.class);
		compatTypes.put(BigDecimal.class, int.class);
	}
	private static int countGend=0;
	private static int countWantd=0;

	private static Map<Method, Method> codeMap = new HashMap<Method, Method>();

	/**This method generates JDBC Dao code by matching the columns with the setter methods
	 * @param set - The Class name for which to set the values to
	 * @param columns- List of Column names
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static String jelperJdbcCodeGen(String set, List<String> columns) throws ClassNotFoundException{
		Class setClass = null;

		//cleanColumns= Util.cleanseColumns(columns);
		setClass = ClassLoader.getSystemClassLoader().loadClass(set);
		List<Method> setClassMethods = JelperUtil.getClassMethods(setClass, SET);
		countWantd=setClassMethods.size();
		if(setClassMethods.size()!=0 && columns.size()!=0){
			gendJdbcCode.append(JelperUtil.getResSetInstCode());
			gendJdbcCode.append(JelperUtil.genInstantiationCode(setClass));
			matchGetterWithColumn(setClassMethods, columns);
		}
		System.out.println(countGend +" setters generated of " + countWantd);
		return gendJdbcCode.toString();
	}

	public static void setMatchLevel(int matchLvl){
		matchLevel= matchLvl;
	}
	private static void matchGetterWithColumn(List<Method> setClassMethods,
			List<String> columns) {
		String setName=null;
		String column=null;
		boolean flag=false;
		for( int i=0 ; i<setClassMethods.size(); ){
			setName= setClassMethods.get(i).getName().substring(3);
			for( int j=0 ; j<columns.size();){
				column= columns.get(j);
				if(JelperUtil.isColumnMatching(setName,column)){
					genJdbcCode(setClassMethods.get(i), column);

					countGend++;
					setClassMethods.remove(i);
					columns.remove(j);
					flag=true;
					i=j=0;
				}
				else{
					j++;
				}
			}
			if(!flag){
				i++;
			}
		}

	}
	/**This method generates setter code on the class instance that is being "set". The method
	 * accesses all the setter methods of the set class and tries to match the getters with the setters
	 * Eg. a setter setDrvrDet() of type Class1 and getter getDriverDetails() Class2 will result in the code,
	 * 		 class1.setDrvrDet(class2.getDriverDetails());
	 * Also the code generated follows dictionary order on the setter methods.
	 *
	 * @param get- The Class name on which getters should be called
	 * @param set- The Class name on which setters should be called
	 * @return returns a String that is the generated code
	 */
	public static String genSetterCode(String get, String set) {
		Class getClass = null;
		Class setClass = null;
		//long start = new Date().getTime();
		try {
			//load the provided java classes
			getClass = ClassLoader.getSystemClassLoader().loadClass(get);
			setClass = ClassLoader.getSystemClassLoader().loadClass(set);
			//extract the methods of both classes
			List<Method> getClassMethods = JelperUtil.getClassMethods(getClass, GET);
			List<Method> setClassInitMethods = JelperUtil.getClassMethods(setClass, SET);
			List<Method> setClassMethods = new LinkedList<Method>();
			// System.out.println(getClassMethods);
			// System.out.println(setClassMethods);

			List<String> setNameList= new ArrayList<String>();
			List<Method> setList= new LinkedList<Method>();
			for ( int i=0; i< setClassInitMethods.size();  i++){
				setNameList.add(setClassInitMethods.get(i).getName());
			}
			Collections.sort(setNameList);
			for( int i=0; i < setNameList.size(); i++){
				for( int j=0 ;j < setClassInitMethods.size(); j++){
					if( setNameList.get(i).equals(setClassInitMethods.get(j).getName())){
						setClassMethods.add( setClassInitMethods.get(j));
						setList.add(setClassInitMethods.get(j));
						break;
					}
				}
			}

			for(int level=1; level<=matchLevel;++level){
				if (getClassMethods.size() > 0 && setClassMethods.size() > 0) {
					matchGetterToSetter(getClassMethods, setClassMethods,
							level);
				}
			}
			generateCode(setList);
			//System.out.println("\nTook :" + (new Date().getTime() - start) + "ms");


		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return gendCode.toString();
	}

	private static void generateCode(List<Method> setClassMethods) {
		// Set<String> stringSet= codeMap.keySet();
		for (Method setClassMethod : setClassMethods) {
			if (codeMap.containsKey(setClassMethod)) {
				genSetCode(setClassMethod,
						codeMap.get(setClassMethod));
			}
		}
	}


	private static void matchGetterToSetter(List<Method> getClassMethods,
											List<Method> setClassMethods, int matchLevel) {
		String setName = null;
		String getName = null;
		String setType= null;
		String getType= null;
		if (PERFECT_MATCH==matchLevel) {
			for (int i = 0; i < setClassMethods.size(); i++) {
				setType = setClassMethods.get(i).getParameterTypes()[0].getSimpleName();
				setName = setClassMethods.get(i).getName().substring(3);
				for (int j = 0; j < getClassMethods.size(); j++) {
					getType = getClassMethods.get(j).getReturnType().getSimpleName();
					getName = getClassMethods.get(j).getName().substring(3);
					if (setName.equals(getName)) {
						codeMap.put(setClassMethods.get(i), getClassMethods.get(j));
						//genSetCode(setClassMethods.get(i), getClassMethods.get(j));
						setClassMethods.remove(i);
						getClassMethods.remove(j);
						i--;//to stop skipping the next element, since removing moves the elements 1 step up
						break;
					}
//					else
//						j++;
				}
			}

		} else if (IGNORE_CASE_IND_WORDS==matchLevel) {

			for (int i = 0; i < setClassMethods.size(); i++) {
				setType = setClassMethods.get(i).getParameterTypes()[0].getSimpleName();
				setName = setClassMethods.get(i).getName().substring(3);
				for (int j = 0; j < getClassMethods.size(); j++) {
					getName = getClassMethods.get(j).getName().substring(3);
					if (setName.equalsIgnoreCase(getName)) {

						codeMap.put(setClassMethods.get(i), getClassMethods.get(j));
						//genSetCode(setClassMethods.get(i), getClassMethods.get(j));
						setClassMethods.remove(i);
						getClassMethods.remove(j);
						i--;
						break;
					}
//					else {					
//						j++;
//					}
				}

			}

		}
		else if(TRY_MATCH_SHORTENED==matchLevel){
			boolean flag=false;
			List<String> setMethWords=null;
			List<String> getMethWords=null;
			for (int i = 0; i < setClassMethods.size(); i++) {
				setType = setClassMethods.get(i).getParameterTypes()[0].getSimpleName();
				setName = setClassMethods.get(i).getName().substring(3);
				for (int j = 0; j < getClassMethods.size(); j++) {
					getName = getClassMethods.get(j).getName().substring(3);
					setMethWords= JelperUtil.getWordsInMethodName(setName);
					getMethWords= JelperUtil.getWordsInMethodName(getName);
					if( JelperUtil.isShortenedMatching( setMethWords, getMethWords)){
						codeMap.put(setClassMethods.get(i), getClassMethods.get(j));
						//genSetCode(setClassMethods.get(i), getClassMethods.get(j));
						setClassMethods.remove(i);
						getClassMethods.remove(j);
						i--;
						break;
					}
//					else {
//						// flag=false;
//						j++;
//					}
				}
			}
		}
		else if(TRY_MATCH_SHORTENED_SHUFFLED==matchLevel){
			boolean flag=false;
			List<String> setMethWords=null;
			List<String> getMethWords=null;
			List<String> getMethWordsLList= null;
			for (int i = 0; i < setClassMethods.size(); i++) {
				setName = setClassMethods.get(i).getName().substring(3);
				for (int j = 0; j < getClassMethods.size(); j++) {
					getName = getClassMethods.get(j).getName().substring(3);
					setMethWords= JelperUtil.getWordsInMethodName(setName);
					getMethWords= JelperUtil.getWordsInMethodName(getName);

					for(int k=0; k< getMethWords.size(); k++){
						flag= false;
						getMethWordsLList =getMethWords;//necessary here
						if( k< getMethWords.size()-1){
							String prev= getMethWordsLList.get(k);
							getMethWordsLList.set(k, getMethWordsLList.get(k+1));
							getMethWordsLList.set(k+1, prev);
						}

						if( JelperUtil.isShortenedMatching( setMethWords, getMethWordsLList)){
							codeMap.put(setClassMethods.get(i), getClassMethods.get(j));
							//genSetCode(setClassMethods.get(i), getClassMethods.get(j));
							setClassMethods.remove(i);
							getClassMethods.remove(j);
							i--;
							flag= true;
							break;
						}
					}

					if(!flag){
						j++;
					}else{
						break;
					}
				}
			}
		}
		else if(MATCH_REMAINING_TYPE==matchLevel){
			for( int i= 0 ; i<setClassMethods.size(); i++){
				if( i < getClassMethods.size()){
					if( setClassMethods.get(i).getParameterTypes()[0].equals(getClassMethods.get(i).getReturnType())){
						codeMap.put(setClassMethods.get(i), getClassMethods.get(i));
					}
					//genSetCode(setClassMethods.get(i), getClassMethods.get(j));
				}
			}
		}

	}

	private static void genSetCode(Method setMethod, Method getMethod) {
		String getObjName = null;
		String setObjName = null;
		getObjName = JelperUtil.formatDeclaringClassNameAsCamel(getMethod.getDeclaringClass());
		setObjName = JelperUtil.formatDeclaringClassNameAsCamel(setMethod.getDeclaringClass());
		gendCode.append(setObjName);
		gendCode.append(PERIOD);
		gendCode.append(setMethod.getName());
		gendCode.append(OPEN_PARAN);
		gendCode.append(JelperUtil.isTypeCastNeeded(setMethod, getMethod));
		//gendCode.append(PAIR_PARAN);
		gendCode.append(CLOSED_PARAN);
		gendCode.append(SEMI_COLON);
		gendCode.append(NEW_LINE);
	}

	private static void genJdbcCode(Method setMethod, String column){
		String setObjName = null;
		String resMethod= JelperUtil.getResultSetGetterMethod(setMethod);
		setObjName = JelperUtil.formatDeclaringClassNameAsCamel(setMethod.getDeclaringClass());
		gendJdbcCode.append(setObjName);
		gendJdbcCode.append(PERIOD);
		gendJdbcCode.append(setMethod.getName());
		gendJdbcCode.append(OPEN_PARAN);
		gendJdbcCode.append("rs");
		gendJdbcCode.append(PERIOD);
		gendJdbcCode.append(resMethod);
		gendJdbcCode.append(OPEN_PARAN);
		gendJdbcCode.append(DOUBLE_QUOTE);
		gendJdbcCode.append(column);
		gendJdbcCode.append(DOUBLE_QUOTE);
		gendJdbcCode.append(CLOSED_PARAN);
		gendJdbcCode.append(CLOSED_PARAN);
		gendJdbcCode.append(SEMI_COLON);
		gendJdbcCode.append(NEW_LINE);
	}
}
