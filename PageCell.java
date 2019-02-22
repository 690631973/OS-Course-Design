public class PageCell {

	int num;
	int addr;
	@Override
	public String toString() {
		return num +":"+addr;
	}
	
	PageCell(int num, int addr) {
		this.num = num;
		this.addr = addr;
	}
	
}
