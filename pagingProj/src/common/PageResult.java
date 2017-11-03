package common;

// 페이지에 대한 모듈 처리 클래스
// 화면에 페이징과 관련된거 다 설정

public class PageResult {
	
	
	private int pageNo, count;
	private int beginPage, endPage;
	private boolean prev, next;
	// jsp에서 쓰는 것들
	
	public PageResult(int pageNo, int count) {
		this.pageNo = pageNo;
		this.count = count;

		// 마지막 페이지 번호 구하기
		int lastPage = (count % 10 == 0) ? count/10 : count/10+1 ; 
		

		
		// TAB 블럭 관련처리 ========================
		
		// 페이 번호에 따른 현재 탭번호
		int currTab = (pageNo - 1) / 10 + 1 ;
		// 탭의 시작
		beginPage = (currTab - 1) * 10 + 1 ;
		// 탭의 종료
		endPage =  (currTab * 10 > lastPage) ? lastPage : currTab * 10;
		
		// 이전이 있으면 true;
		prev = beginPage != 1;
		next = endPage != lastPage;
	
	}

	public int getPageNo() {
		return pageNo;
	}

	public int getCount() {
		return count;
	}

	public int getBeginPage() {
		return beginPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public boolean getPrev() {
		return prev;
	}

	public boolean getNext() {
		return next;
	}
	
	
	
}
