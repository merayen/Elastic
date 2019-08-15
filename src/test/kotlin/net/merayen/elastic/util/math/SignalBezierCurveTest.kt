package net.merayen.elastic.util.math

import org.junit.jupiter.api.Test

class SignalBezierCurveTest {
	@Test
	fun testIt() {
		val dot0 = BezierCurve.Dot()
		val dot1 = BezierCurve.Dot()

		dot0.position.x = 0f
		dot0.position.y = 0.5f
		dot0.right.x = 0.5f
		dot0.right.y = 0f

		dot1.position.x = 1f
		dot1.position.y = 0.5f
		dot1.left.x = 0.5f
		dot1.left.y = 1f

		val ITERATIONS = 100
		val result = FloatArray(ITERATIONS)

		SignalBezierCurve.getValuesFromSegment(dot0, dot1, result, 0, result.size)

		// TODO fix test. Actually make bezier sinus-like? Is it possible?

		/*int i = 0; // Doesn't work. bezier very different from sine waves?
		for(float r : result) {
			double fasit = Math.sin(-Math.PI * 2 * (i++ / (float)ITERATIONS)) / 6.95 + 0.5f;
			System.out.println(String.format("%.3f - %.3f (%.3f)\t-> %.3f%%", r, fasit, fasit - r, Math.abs(r - 0.5) / Math.abs(fasit - 0.5) * 100));
			if(Math.abs(fasit - r) > 0.01)
				no();
		}*/
	}
}