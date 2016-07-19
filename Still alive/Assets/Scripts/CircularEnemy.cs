using UnityEngine;
using System.Collections;

public class CircularEnemy : MonoBehaviour {

	public Vector3 center;
	public float velocityInDegrees = -65.0f;

	private Vector3 v;

	void Start() {
		v = transform.position - center;
	}

	void Update () {
		
		v = Quaternion.AngleAxis (velocityInDegrees * Time.deltaTime, Vector3.forward) * v;
		transform.position = center + v;
	}
}
