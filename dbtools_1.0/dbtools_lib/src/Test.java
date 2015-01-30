import com.dbm.common.util.SecuUtil;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s1 = "jfds!@#$%$&";
		String s2 = SecuUtil.encryptRSA(s1);
		String s3 = SecuUtil.decryptRSA(s2);
System.out.println(s1);
System.out.println(s2);
System.out.println(s3);
	}

}
