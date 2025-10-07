extends Control


func _on_back_pressed() -> void:
	PianoManager.to_main_menu()


func _on_scan_devices_pressed() -> void:
	PianoManager.scan_for_devices()
