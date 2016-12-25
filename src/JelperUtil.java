import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;





public class JelperUtil {

	protected static List<Method> getClassMethods(Class classObj,String meth) {
		List<Method> methods= new ArrayList<Method>();
		Method[] methodArr=classObj.getDeclaredMethods();
		 
		for( int i=0 ;i<methodArr.length; i++){
			if( meth.equalsIgnoreCase(methodArr[i].getName().substring(0,3))){
				methods.add(methodArr[i]);
			}
		}
		//methods= new ArrayList(Arrays.asList(classObj.getDeclaredMethods()));		
		return methods;
	}

	protected static String formatDeclaringClassNameAsCamel(Class classOb) {
		String str=classOb.getName();
		str=str.replaceFirst(str.substring(0, 1),str.substring(0, 1).toLowerCase() );
		return str;
	}

	protected static List<String> getWordsInMethodName(String name) {
		List<String> words= new ArrayList<String>();
		name=(name.substring(0,3).equals("set") || name.substring(0,3).equals("get"))?name.substring(3):name;//remove "set" or "get"
		int j=0;
		for( int i =1 ; i< name.length(); i++){
			if( name.charAt(i) > 64 && name.charAt(i)< 91){
				words.add(name.substring(j, i));
				j=i;
			}
			if(i==name.length()-1){
				words.add(name.substring(j));
			}
		}
		return words;
	}

	protected static boolean isWordsMatching(Method setMethod,
			Method getMethod) {
				return false;/*
		int count=-1;
		setMethod.getName().
		if(setMethodWords.size() == getMethodWords.size()){
			
			for( int i =0 ; i< setMethodWords.size(); i++){
				if(setMethodWords.get(i).equalsIgnoreCase(getMethodWords.get(i))){
					count++;
				}
			}
		}
		if(count == setMethodWords.size())
			return true;
		return false;
	*/}

	protected static String getResultSetGetterMethod(Method setMethod) {
		StringBuilder str= new StringBuilder();
		str.append("get");
		String qualType=setMethod.getParameterTypes()[0].getName();
		String type= capitalizeFirstLetter(qualType.substring(qualType.lastIndexOf(".")+1));
		str.append(type);
		return str.toString();
	}

	private static String capitalizeFirstLetter(String substring) {
		String first=substring.substring(0,1);
		substring= substring.replaceFirst(first, first.toUpperCase());
		return substring;
	}

	protected static boolean isColumnMatching(String name, String column) {
		if( name.equalsIgnoreCase(column.replaceAll("[_-]","")))
			return true;
		return false;
		
	}

	protected static String genInstantiationCode(Class classOb) {
			StringBuilder sb= new StringBuilder();
			sb.append(classOb.getName());
			sb.append(" ");
			sb.append(formatDeclaringClassNameAsCamel(classOb));
			sb.append(" = ");
			sb.append("new");
			sb.append(" ");
			sb.append(classOb.getName());
			sb.append("()");
			sb.append(";");
			sb.append("\n");
			return sb.toString();
		
	}

	protected static String getResSetInstCode() {
		StringBuilder code= new StringBuilder();
		code.append("Statement stmt = connection.createStatement();");
		code.append("\n");
		code.append("ResultSet rs = stmt.executeQuery(query);");
		code.append("\n");
		return code.toString();
	}

	protected static boolean isShortenedMatching(List<String> setMethWords,
			List<String> getMethWords) {
		String setWord, getWord;
		int count=0;
		//try matching iff both methods have same number of words
		if( setMethWords.size()==getMethWords.size()){
			for( int i=0; i< setMethWords.size(); i++){
				setWord= setMethWords.get(i);
				getWord= getMethWords.get(i);
				if( setWord.equalsIgnoreCase(getWord))
					count++;
				else{
					if(!isWordMatchingShortened( setWord, getWord)){//if there is even one mismatch between words
																	//there is no point in continuing , return false
						return false;
					}else{
						count++;
					}
				}
				if( count==setMethWords.size()){
					return true;
				}
			}
		}
		return false;
	}

	
	private static boolean isWordMatchingShortened(String setWord, String getWord) {
		String shortStr, longStr;
		if( setWord.length()<getWord.length()){
			shortStr=setWord;
			longStr=getWord;
		}else{
			shortStr=getWord;
			longStr=setWord;
		}
		int j=0;

		if(shortStr.length()< 2 || longStr.charAt(0)!=shortStr.charAt(0))//if the first letter not ewual then false straight away
			return false;

		for( int i=0 ; i< shortStr.length() && j< longStr.length(); i++, j++){// to see if the letters in shortStr are occuring in longStr in order
			if(! longStr.substring(j,j+1).equals(shortStr.substring(i,i+1))){
				--i;//keep the short string pointer at the same place in next loop to wait for it to match in next iter
			}
			if( i== shortStr.length()-1)//if all the letters are in order
				return true;
		}
		return false;
	}

	protected static String isTypeCastNeeded(Method setMethod, Method getMethod) {
		String getObjName=null;
		getObjName = JelperUtil.formatDeclaringClassNameAsCamel(getMethod.getDeclaringClass());
		StringBuilder code= new StringBuilder();
		Class set= setMethod.getParameterTypes()[0];
		if( !set.equals(getMethod.getReturnType()) &&
				Jelper.compatTypes.containsKey(set)){
			if( Jelper.compatTypes.get(set).equals(getMethod.getReturnType()))
				code.append("new " + set.getSimpleName() + Jelper.OPEN_PARAN +Jelper.CLOSED_PARAN);
				code.insert(code.toString().lastIndexOf(")"),getObjName + Jelper.PERIOD + getMethod.getName() + Jelper.PAIR_PARAN);
				return code.toString();
			}
//			getObjName = Util.formatDeclaringClassNameAsCamel(getMethod.getDeclaringClass());
//			return getObjName + Jelper.PERIOD + getMethod.getName();
		else {
			code.append(getObjName + Jelper.PERIOD + getMethod.getName()  + Jelper.PAIR_PARAN);
			return code.toString();
		}
	}
}
