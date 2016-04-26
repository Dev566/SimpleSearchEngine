package dev.search.com;

public class InvertedList {
	private int DocumentID;
	private int Frequency;
	private int[] Position = new int[200];
	private int PositionCount = 0;
	private InvertedList NxtList;

	public void AddPosition(int pPosition) {
		Position[PositionCount++] = pPosition;
	}

	public InvertedList(int pDocID, int pFrequency, int pPosition) {
		DocumentID = pDocID;
		Frequency = pFrequency;
		AddPosition(pPosition);
		NxtList = null;
	}

	public int GetDocID() {

		return DocumentID;
	}

	public InvertedList GetNextNode() {

		return this.NxtList;

	}

	public int GetFrequency() {
		return this.Frequency;
	}

	public void AddFrequency(int pPosition) {

		Frequency++;
		//AddPosition(pPosition);
	}

	public void AddNxtNode(InvertedList pNxtNode) {
		this.NxtList = pNxtNode;
	}

}
