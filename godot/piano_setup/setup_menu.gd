extends Control

@onready var confrim_dialog: ConfirmDialog = %ConfirmDialog

func _ready() -> void:
	PianoManager.confirm_bluetooth_access.connect(_open_confirm_bluetooth_access)
	PianoManager.confirm_location_access.connect(_open_confirm_location_access)

func _on_back_pressed() -> void:
	PianoManager.to_main_menu()


func _on_scan_devices_pressed() -> void:
	PianoManager.scan_for_devices()

func _open_confirm_bluetooth_access() -> void:
	confrim_dialog.customize("Allow bluetooth access", "To find the piano the app needs to access bluetooth.", "Allow", "Cancel")
	confrim_dialog.show()
	confrim_dialog.confirmed.connect(_confirm_bluetooth_access_callback)
	
func _confirm_bluetooth_access_callback(confirmed: bool) -> void:
	confrim_dialog.confirmed.disconnect(_confirm_bluetooth_access_callback)
	
	if(confirmed):
		PianoManager.enable_bluetooth();

func _open_confirm_location_access() -> void:
	confrim_dialog.customize("Allow location access", "To find the piano the app needs to access to the location of the phone.", "Allow", "Cancel")
	confrim_dialog.show()
	confrim_dialog.confirmed.connect(_confirm_location_access_callback)
	
func _confirm_location_access_callback(confirmed: bool) -> void:
	confrim_dialog.confirmed.disconnect(_confirm_location_access_callback)
	
	if(confirmed):
		PianoManager.enable_location()
