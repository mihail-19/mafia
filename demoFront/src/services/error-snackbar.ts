import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError} from 'rxjs';
import { Router } from '@angular/router';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
@Injectable({
  providedIn: 'root'
})
export class ErrorSnackbar
 {
	constructor(public snackBar: MatSnackBar){}
	private config: MatSnackBarConfig = {
		duration: 3000,
		horizontalPosition: "left",
		verticalPosition: "top",
		panelClass: "error-snackbar"
	}
	show(msg:string){
		this.snackBar.open(msg, '', this.config);
	}
}