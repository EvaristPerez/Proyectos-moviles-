using UnityEngine;
using System.Collections;

public class ComplexRouteEnemy : MonoBehaviour {

	public Vector2 pointB, pointC, pointD;
	public float timeAB = 1,  timeBC = 1, timeCD= 1, timeDA = 1;

	IEnumerator Start()
	{
		var pointA = transform.position;
		while (true) {
			yield return StartCoroutine(MoveObject(transform, pointA, pointB, timeAB));
			yield return StartCoroutine(MoveObject(transform, pointB, pointC, timeBC));
			yield return StartCoroutine(MoveObject(transform, pointC, pointD, timeCD));
			yield return StartCoroutine(MoveObject(transform, pointD, pointA, timeDA));

		}
	}

	IEnumerator MoveObject(Transform thisTransform, Vector2 startPos, Vector2 endPos, float time)
	{
		var i= 0.0f;
		var rate= 1.0f/time;
		while (i < 1.0f) {
			i += Time.deltaTime * rate;
			thisTransform.position = Vector2.Lerp(startPos, endPos, i);
			yield return null; 
		}
	}
}
