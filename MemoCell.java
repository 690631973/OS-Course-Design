public class MemoCell {
	@Override
	public String toString() {
		return addr+":"+data;
	}
	int addr;
	int data;
	MemoCell(int addr,int num) {
		this.addr= addr;
		this.data=num;
	}
	
}
