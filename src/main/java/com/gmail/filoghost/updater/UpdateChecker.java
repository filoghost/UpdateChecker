/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.gmail.filoghost.updater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * A very simple and lightweight update checker, without download features.
 * @author filoghost
 */
public final class UpdateChecker {

	/**
	 * This method creates a new async thread to check for updates.
	 */
	public static void run(final Plugin plugin, final int projectId, final ResponseHandler responseHandler) {
		if (plugin == null) {
			throw new NullPointerException("Plugin cannot be null");
		}
		if (responseHandler == null) {
			throw new NullPointerException("ResponseHandler cannot be null");
		}
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					JSONArray filesArray = (JSONArray) readJson("https://api.curseforge.com/servermods/files?projectIds=" + projectId);

					if (filesArray.size() == 0) {
						// The array cannot be empty, there must be at least one file.
						// The project ID is not valid or curse returned a wrong response.
						return;
					}
					
					String updateName = (String) ((JSONObject) filesArray.get(filesArray.size() - 1)).get("name");
					final PluginVersion remoteVersion = new PluginVersion(updateName);
					PluginVersion localVersion = new PluginVersion(plugin.getDescription().getVersion());
					
					if (remoteVersion.isNewerThan(localVersion)) {
						// Run synchronously on main thread
						Bukkit.getScheduler().runTask(plugin, new Runnable() {

							@Override
							public void run() {
								responseHandler.onUpdateFound(remoteVersion.getFormattedVersion());
							}
							
						});
					}

				} catch (IOException e) {
					plugin.getLogger().log(Level.WARNING, "Could not contact BukkitDev to check for updates.");
				} catch (InvalidVersionException e) {
					plugin.getLogger().log(Level.WARNING, "Could not check for updates because of a version format error: " + e.getMessage() + ".");
					plugin.getLogger().log(Level.WARNING, "Please notify the author of this error.");
				} catch (Exception e) {
					plugin.getLogger().log(Level.WARNING, "Unable to check for updates: unhandled exception.", e);
				}
			}
			
		}).start();
	}
	
	
	private static Object readJson(String url) throws MalformedURLException, IOException {
		URLConnection conn = new URL(url).openConnection();
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(8000);
		conn.addRequestProperty("User-Agent", "Plugin Updater (by filoghost)");
		conn.setDoOutput(true);

		return JSONValue.parse(new BufferedReader(new InputStreamReader(conn.getInputStream())));
	}
	
}
