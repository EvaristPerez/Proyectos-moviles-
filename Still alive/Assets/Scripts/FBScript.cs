using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using System.Collections.Generic;
using Facebook.Unity;

public class FBScript : MonoBehaviour {

	void Awake()
	{
		FB.Init (SetInit, OnHideUnity);
	}

	void SetInit()
	{

		if (FB.IsLoggedIn) {
			Debug.Log ("FB is logged in");
		} else {
			Debug.Log ("FB is not logged in");
		}


	}

	void OnHideUnity(bool isGameShown)
	{

		if (!isGameShown) {
			Time.timeScale = 0;
		} else {
			Time.timeScale = 1;
		}

	}

	public void FBlogin()
	{
		List<string> permissions = new List<string> ();
		permissions.Add ("public_profile");

		FB.LogInWithReadPermissions (permissions, AuthCallBack);

	}

	void AuthCallBack(IResult result)
	{

		if (result.Error != null) {
			Debug.Log (result.Error);
		} else {
			if (FB.IsLoggedIn && !result.Cancelled) {
				Debug.Log ("FB is logged in");
				Share ();

			} else {
				Debug.Log ("FB is not logged in");
			}

		}

	}

	public void Share()
	{		
		FB.FeedShare(
			string.Empty, //toId
			new System.Uri("https://play.google.com/store"), //link
			"Still Alive", //linkName
			"Disponible en Google Play", //linkCaption
			"He completado un nivel de Still Alive, ¡Descárgatelo ya!", //linkDescription
			new System.Uri("https://dl.dropboxusercontent.com/u/44496394/ShareImage.png"),
			string.Empty,
			ShareCallback //callback
		);

	}

	void ShareCallback(IResult result)
	{
		if (result.Cancelled) {
			Debug.Log ("Share Cancelled");
		} else if (!string.IsNullOrEmpty (result.Error)) {
			Debug.Log ("Error on share!");
		} else if (!string.IsNullOrEmpty (result.RawResult)) {
			Debug.Log ("Success on share");
		}
	}
}

