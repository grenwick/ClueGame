package tests;

public class disregard {

	@Test
	public void testScreens() {
		CentralComputer testCenter = new CentralComputer();
		OLEDvideo testVideo = new OLEDvideo();
		OLEDpic testPic = new OLEDpic();
		LineDisplay testLine = new LineDisplay();
		testCenter.addScreen(testVideo);
		testCenter.addScreen(testPic);
		testCenter.addScreen(testLine);
		testCenter.pushMessage("test");
		assertEquals(testCenter.getScreen[2].getCurrentDisplay(), "test");
		assertTrue(testCenter.getScreen[1].getCurrentDisplay() != null);
		assertTrue(testCenter.getScreen[0].getCurrentDisplay() == Screen.format("test"));
		
		
	}
}
