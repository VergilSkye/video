import { Injectable } from '@angular/core';
import { throwError as observableThrowError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { SERVER_API_URL } from 'app/app.constants';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class VideoSessionService {
  public resourceUrl = SERVER_API_URL + 'api';

  constructor(protected http: HttpClient) {}

  createSession(consultaId: number) {
    return this.http.post(`${this.resourceUrl}/create-session/${consultaId}`, null, { observe: 'response' }).pipe(
      map(response => {}),
      catchError(error => this.handleError(error))
    );
  }
  generateToken(consultaId: number) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const options = {
      headers: headers
    };
    return this.http.post(`${this.resourceUrl}/generate-token/${consultaId}`, null, options).pipe(
      map(res => {
        console.log('OpenVidu sessionId and token retrieval SUCCESS. Response: ', res);
        return res;
      }),
      catchError(error => this.handleError(error))
    );
  }

  removeUser(consultaId: number) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const options = {
      headers: headers
    };
    return this.http.post(`${this.resourceUrl}/remove-user/${consultaId}`, null, options).pipe(
      map(res => res || []),
      catchError(error => this.handleError(error))
    );
  }

  private handleError(error: any) {
    console.error(error);
    return observableThrowError(error);
  }
}
