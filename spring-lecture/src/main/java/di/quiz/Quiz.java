package di.quiz;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Quiz {
	public static void main(String[] args) {
		ApplicationContext container = new ClassPathXmlApplicationContext(
					"di/quiz.xml"
			);
		/// 주입을 먼저다 하고 나머지 실행한다.
		/// 매우 중요!!
		/// 여기서 모든 인스턴스화와 객체주입을 다 한다.
		
		Person p = (Person)container.getBean("user");
		Animal ani = p.getAnimal();
		ani.info();
		// 화면에 "성공" 출력됨...
		
	}
}


