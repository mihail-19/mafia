import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS, HttpRequest, HttpHandler, HttpInterceptor  } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule } from '@angular/forms';
import { Injectable } from '@angular/core';
import {AuthService} from '../services/auth.service';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { GamesComponent } from './games/games.component';
import { MatDialogModule} from '@angular/material/dialog';
import { RulesComponent } from './rules/rules.component';
@Injectable()
export class XhrInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const xhr = req.clone(
     {withCredentials: true}
    );
    return next.handle(xhr);
  }
}
@NgModule({
  declarations: [
    AppComponent,
    GamesComponent,
    RulesComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
	HttpClientModule,
	FormsModule,
	MatSnackBarModule,
	BrowserAnimationsModule,
	MatDialogModule
  ],
  providers: [AuthService, { provide: HTTP_INTERCEPTORS, useClass: XhrInterceptor, multi: true }],
  bootstrap: [AppComponent]
})
export class AppModule { }
