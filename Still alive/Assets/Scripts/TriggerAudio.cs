using UnityEngine;
using System.Collections;

public class TriggerAudio : MonoBehaviour {

	AudioSource audioSource;

	void Start()
	{
		audioSource = GetComponent<AudioSource>();
	}

	public void Play() { 

		audioSource.PlayOneShot (audioSource.clip);
	}
}
