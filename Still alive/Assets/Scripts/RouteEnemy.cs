using UnityEngine;
using System.Collections;

public class RouteEnemy : MonoBehaviour
{
	public Vector2 pointB;
	public float time;

	IEnumerator Start()
	{
		var pointA = transform.position;
		while (true) {
			yield return StartCoroutine(MoveObject(transform, pointA, pointB, time));
			yield return StartCoroutine(MoveObject(transform, pointB, pointA, time));
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