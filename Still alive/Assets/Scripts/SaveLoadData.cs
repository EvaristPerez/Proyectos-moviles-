using UnityEngine;
using System;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;

[Serializable]
public abstract class SaveGame
{
	
}

public static class SaveGameSystem
{
	public static bool SaveGame(SaveGame saveGame, string name)
	{
		BinaryFormatter formatter = new BinaryFormatter();

		using (FileStream stream = new FileStream(GetSavePath(name), FileMode.Create))
		{
			try
			{
				formatter.Serialize(stream, saveGame);
			}
			catch (Exception)
			{
				return false;
			}
		}

		return true;
	}

	public static SaveGame LoadGame(string name)
	{
		if (!DoesSaveGameExist(name))
		{
			return null;
		}

		BinaryFormatter formatter = new BinaryFormatter();

		using (FileStream stream = new FileStream(GetSavePath(name), FileMode.Open))
		{
			try
			{
				return formatter.Deserialize(stream) as SaveGame;
			}
			catch (Exception)
			{
				return null;
			}
		}
	}

	public static bool DeleteSaveGame(string name)
	{
		try
		{
			File.Delete(GetSavePath(name));
		}
		catch (Exception)
		{
			return false;
		}

		return true;
	}

	public static bool DoesSaveGameExist(string name)
	{
		return File.Exists(GetSavePath(name));
	}

	private static string GetSavePath(string name)
	{
		return Path.Combine(Application.persistentDataPath, name + ".sav");
	}
}

[Serializable]
public class MySaveGame : SaveGame
{
	public bool[] nivelesBloqueado = new bool[]{false, true, true, true, true};
	public int[] medallasNivel = new int[]{0,0,0,0,0};
	public bool sonido = true;

	public void setSonido (bool musica)
	{
		sonido = musica;
	}

	public bool getSonido()
	{
		return sonido;
	}

	public void setNivelBloqueado(int nivel, bool bloqueado)
	{
		nivelesBloqueado [nivel-1] = bloqueado;
	}

	public void setMedallasNivel(int nivel, int medallas)
	{
		medallasNivel [nivel-1] = medallas;
	}

	public bool[] getNivelesBloqueados()
	{
		return nivelesBloqueado;
	}

	public int[] getMedallas()
	{
		return medallasNivel;
	}

	public bool getNivelBloqueado(int level)
	{
		return nivelesBloqueado[level-1];
	}

	public int getMedallasNivel(int level)
	{
		return medallasNivel[level-1];
	}

}