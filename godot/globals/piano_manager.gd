extends Node

var save_file_path = 'user:://save-game-0.0.0.save'

var connector_plugin_name = "PianoConnectorPlugin"
var connector_plugin

const BLUETOOTH_SCAN_STARTED = "started"
const BLUETOOTH_SCAN_LOCATION_DISABLED = "locationDisabled"
const BLUETOOTH_SCAN_NEW_DEVICES = "newDevices"
const BLUETOOTH_SCAN_STOPPED = "stopped"

var is_loaded = false
var is_connected = false

func _ready() -> void:
	if Engine.has_singleton(connector_plugin_name):
		connector_plugin = Engine.get_singleton(connector_plugin_name)
		connector_plugin.bluetoothHandler.connect(_handle_bluetooth_event)
		is_loaded = true
	else:
		is_loaded = false
		printerr("Couldn't find plugin " + connector_plugin_name)


func _load_save():
	var save_file = FileAccess.open(save_file_path, FileAccess.READ)
	# TODO add save file logic


func switch_scene(path: String) -> void:
	get_tree().call_deferred("change_scene_to_file", path)


func _handle_bluetooth_event(event: String) -> void:
	print("Event: " + event);
		
	if(event == BLUETOOTH_SCAN_LOCATION_DISABLED):
		pass
		

	print("devices: " + connector_plugin.getScanResults())
	

func scan_for_devices() -> void:
	connector_plugin.scanBLEDevices()


func start_game() -> void:
	if !is_connected:
		switch_scene("res://piano_setup/setup_menu.tscn")


func to_main_menu() -> void:
	switch_scene("res://main_menu/main_menu.tscn")
	
	
