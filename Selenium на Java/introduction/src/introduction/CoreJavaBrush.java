package introduction;

import java.util.ArrayList;

public class CoreJavaBrush {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int myNum = 5;
		String world = "hello";
		char letter = 'r';
		double dec = 0.55;
		boolean myCard = true;

		//System.out.println(world);

		int[] arr = new int [5];
		arr[0] = 1;
		arr[1] = 2;
		arr[2] = 3;
		arr[3] = 4;
		arr[4] = 5;
		
		int[] arr2 = {1, 2, 3, 4, 5};
//		for (int i=0; i<arr.length; i++)
//		{
//		  System.out.println(arr2[i]);
//		}

		for (int i: arr2)
		{
		  //System.out.println(i);
		}

		//break — это просто выход из цикла for.

		ArrayList<String> a = new ArrayList<String>();
		a.add("odin");
		//System.out.println(a.get(0));

		String s1 = "dfd rr44 kll";
		String[] chars = s1.split("rr44");
		//System.out.println(chars[0]);
		//System.out.println(chars[1].trim()); //убирает пробел

		for (int i = 0; i<s1.length(); i++) {
			//System.out.println(s1.charAt(i));
		}

		for (int i = s1.length()-1; i>=0; i--) {
			//System.out.println(s1.charAt(i));
		}

		CoreJavaBrush aer = new CoreJavaBrush();
		First();

		Toor s = new Toor();
		s.First();
	}

	public static String First() {
		System.out.println("Work");
		return "work it";
	}
}
