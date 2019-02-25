public class PageCell {

	int num;
	int addr;
	int pri;
	@Override
	public String toString() {
		return num +":"+addr;
	}
	
	PageCell(int num, int addr,int pri) {
		this.num = num;
		this.addr = addr;
		this.pri=pri;
	}
	
}
