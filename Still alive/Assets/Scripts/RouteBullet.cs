using UnityEngine;
using System.Collections;

public class RouteBullet : MonoBehaviour {

	public Vector2 pointB;
	public float timePath, timeWait;

	Vector2 pointA;

	IEnumerator Start()
	{
		pointA = transform.position;
		while (true) {
			yield return StartCoroutine(MoveObject(transform, pointA, pointB, timePath, timeWait));
		}
	}

	IEnumerator MoveObject(Transform thisTransform, Vector2 startPos, Vector2 endPos, float timePath, float timeWait)
	{
		var i= 0.0f;
		var rate= 1.0f/timePath;

		initBullet ();

		while (i < 1.0f) {
			i += Time.deltaTime * rate;
			thisTransform.position = Vector2.Lerp(startPos, endPos, i);
			yield return null; 
		}

		removeBullet ();
		yield return new WaitForSeconds(timeWait); 

	}

	void removeBullet()
	{
		GetComponent<SpriteRenderer> ().enabled = false;
		GetComponent<PolygonCollider2D> ().enabled = false;
		transform.position = pointA;
	}

	void initBullet()
	{
		GetComponent<SpriteRenderer> ().enabled = true;
		GetComponent<PolygonCollider2D> ().enabled = true;
	}
}
