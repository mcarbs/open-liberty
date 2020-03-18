/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.message;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.Bus;
import org.apache.cxf.continuations.ContinuationProvider;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

public class MessageImpl extends StringMapImpl implements Message {
    private static final long serialVersionUID = -3020763696429459865L;

    private Exchange exchange;
    private String id;
    private InterceptorChain interceptorChain;

    // array of Class<T>/T pairs for contents
    private Object[] contents = new Object[20];
    private int index;

    private Object protoHeaders = NOT_FOUND;
    private Object opStack = NOT_FOUND;
    private Object contentType = NOT_FOUND;
    private Object destination = NOT_FOUND;
    private Object queryString = NOT_FOUND;
    private Object httpRequest = NOT_FOUND;
    private Object httpResponse = NOT_FOUND;
    private Object pathToMatchSlash = NOT_FOUND;
    private Object httpRequestMethod = NOT_FOUND;
    private Object interceptorProviders = NOT_FOUND;
    private Object templateParameters = NOT_FOUND;
    private Object accept = NOT_FOUND;
    private Object continuationProvider = NOT_FOUND;
    private Object wsdlDescription = NOT_FOUND;
    private Object wsdlInterface = NOT_FOUND;
    private Object wsdlOperation = NOT_FOUND;
    private Object wsdlPort = NOT_FOUND;
    private Object wsdlService = NOT_FOUND;
    private Object requestUrl = NOT_FOUND;
    private Object requestUri = NOT_FOUND;
    private Object pathInfo = NOT_FOUND;
    private Object basePath = NOT_FOUND;
    private Object fixedParamOrder = NOT_FOUND;
    private Object inInterceptors = NOT_FOUND;
    private Object outInterceptors = NOT_FOUND;
    private Object responseCode = NOT_FOUND;
    private Object attachments = NOT_FOUND;
    private Object encoding = NOT_FOUND;
    
    private static final String REQUEST_PATH_TO_MATCH_SLASH = "path_to_match_slash";
    private static final String TEMPLATE_PARAMETERS = "jaxrs.template.parameters";
    private static final String CONTINUATION_PROVIDER = ContinuationProvider.class.getName();
    private static final String DESTINATION = Destination.class.getName();
    private static final String OP_RES_INFO_STACK = "org.apache.cxf.jaxrs.model.OperationResourceInfoStack";
    private static final Set<String> KEYS;

    static {
        Set<String> keys = new HashSet<String>();
        keys.add(CONTENT_TYPE);
        keys.add(PROTOCOL_HEADERS);
        keys.add(QUERY_STRING);
        keys.add(AbstractHTTPDestination.HTTP_REQUEST);
        keys.add(AbstractHTTPDestination.HTTP_RESPONSE);
        keys.add(REQUEST_PATH_TO_MATCH_SLASH);
        keys.add(HTTP_REQUEST_METHOD);
        keys.add(INTERCEPTOR_PROVIDERS);
        keys.add(TEMPLATE_PARAMETERS);
        keys.add(ACCEPT_CONTENT_TYPE);
        keys.add(CONTINUATION_PROVIDER);
        keys.add(DESTINATION);
        keys.add(OP_RES_INFO_STACK);
        keys.add(WSDL_DESCRIPTION);
        keys.add(WSDL_INTERFACE);
        keys.add(WSDL_OPERATION);
        keys.add(WSDL_PORT);
        keys.add(WSDL_SERVICE);
        keys.add(REQUEST_URL);
        keys.add(REQUEST_URI);
        keys.add(PATH_INFO);
        keys.add(BASE_PATH);
        keys.add(FIXED_PARAMETER_ORDER);
        keys.add(IN_INTERCEPTORS);
        keys.add(OUT_INTERCEPTORS);
        keys.add(RESPONSE_CODE);
        keys.add(ATTACHMENTS);
        keys.add(ENCODING);
        KEYS = Collections.unmodifiableSet(keys);
    }

    // Liberty change - used to avoid resize
    public MessageImpl(int isize, float factor) {
        super(isize, factor);
    }

    public MessageImpl() {
        //nothing
    }

    public MessageImpl(Message m) {
        super(m);
        if (m instanceof MessageImpl) {
            MessageImpl impl = (MessageImpl) m;
            exchange = impl.getExchange();
            id = impl.id;
            interceptorChain = impl.interceptorChain;
            contents = impl.contents;
            index = impl.index;
        } else {
            throw new RuntimeException("Not a MessageImpl! " + m.getClass());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Attachment> getAttachments() {
        return attachments == NOT_FOUND ? null : (Collection<Attachment>) attachments;
    }

    @Override
    public void setAttachments(Collection<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getAttachmentMimeType() {
        //for sub class overriding
        return null;
    }

    @Override
    public Destination getDestination() {
        //System.out.println("***JTD: returningDest " + get(Destination.class));
        //Thread.dumpStack();

        //return get(Destination.class);
        return destination == NOT_FOUND ? null : (Destination) destination;
    }

    @Override
    public Exchange getExchange() {
        return exchange;
    }

    @SuppressWarnings("rawtypes")
    public Map getProtocolHeaders() {
        //System.out.println("***JTD: getProtocolHeaders returning " + protoHeaders);
        return protoHeaders == NOT_FOUND ? null : (Map) protoHeaders;
    }
    
    @SuppressWarnings("rawtypes")
    public void setProtocolHeaders(Map protoHeaders) {
        //System.out.println("***JTD: setProtocolHeaders setting " + protoHeaders);
        this.protoHeaders = protoHeaders;
    }

    public Object getOperationResourceInfoStack() {
        //System.out.println("***JTD: getORIStack returning " + opStack);
        return opStack == NOT_FOUND ? null: opStack;
    }
    
    public void setOperationResourceInfoStack(Object opStack) {
        //System.out.println("***JTD: setORIStack setting " + opStack);
        this.opStack = opStack;
    }

    public String getContentType() {
        //System.out.println("***JTD: getContentType returning " + contentType);
        return contentType == NOT_FOUND ? null : (String) contentType;
    }
    
    public void setContentType(String contentType) {
        //System.out.println("***JTD: setContentType setting " + contentType);
        this.contentType = contentType;
    }

    public Object getHttpRequest() {
        return httpRequest == NOT_FOUND ? null : httpRequest;
    }
    
    public void setHttpRequest(Object httpRequest) {
        this.httpRequest = httpRequest;
    }
    
    public Object getHttpResponse() {
        return httpResponse == NOT_FOUND ? null : httpResponse;
    }
    
    public void setHttpResponse(Object httpResponse) {
        this.httpResponse = httpResponse;
    }

    public Object getAccept() {
        return accept == NOT_FOUND ? null : accept;
    }
    
    public void setAccept(Object accept) {
        this.accept = accept;
    }

    public Object getContinuationProvider() {
        return continuationProvider == NOT_FOUND ? null : continuationProvider;
    }
    
    public void setContinuationProvider(Object continuationProvider) {
        this.continuationProvider = continuationProvider;
    }

    public Object getWsdlDescription() {
        return wsdlDescription == NOT_FOUND ? null : wsdlDescription;
    }
    
    public void setWsdlDescription(Object wsdlDescription) {
        this.wsdlDescription = wsdlDescription;
    }

    public Object getWsdlInterface() {
        return wsdlInterface == NOT_FOUND ? null : wsdlInterface;
    }
    
    public void setWsdlInterface(Object wsdlInterface) {
        this.wsdlInterface = wsdlInterface;
    }

    public Object getWsdlOperation() {
        return wsdlOperation == NOT_FOUND ? null : wsdlOperation;
    }
    
    public void setWsdlOperation(Object wsdlOperation) {
        this.wsdlOperation = wsdlOperation;
    }

    public Object getWsdlPort() {
        return wsdlPort == NOT_FOUND ? null : wsdlPort;
    }
    
    public void setWsdlPort(Object wsdlPort) {
        this.wsdlPort = wsdlPort;
    }

    public Object getWsdlService() {
        return wsdlService == NOT_FOUND ? null : wsdlService;
    }
    
    public void setWsdlService(Object wsdlService) {
        this.wsdlService = wsdlService;
    }

    public Object getRequestUrl() {
        return requestUrl == NOT_FOUND ? null : requestUrl;
    }
    
    public void setRequestUrl(Object requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Object getRequestUri() {
        return requestUri == NOT_FOUND ? null : requestUri;
    }
    
    public void setRequestUri(Object requestUri) {
        this.requestUri = requestUri;
    }
    
    public Object getPathInfo() {
        return pathInfo == NOT_FOUND ? null : pathInfo;
    }
    
    public void setPathInfo(Object pathInfo) {
        this.pathInfo = pathInfo;
    }
    
    public Object getBasePath() {
        return basePath == NOT_FOUND ? null : basePath;
    }
    
    public void setBasePath(Object basePath) {
        this.basePath = basePath;
    }

    public Object getFixedParamOrder() {
        return fixedParamOrder == NOT_FOUND ? null : fixedParamOrder;
    }
    
    public void setFixedParamOrder(Object fixedParamOrder) {
        this.fixedParamOrder = fixedParamOrder;
    }

    public Object getInInterceptors() {
        return inInterceptors == NOT_FOUND ? null : inInterceptors;
    }
    
    public void setInInterceptors(Object inInterceptors) {
        this.inInterceptors = inInterceptors;
    }

    public Object getOutInterceptors() {
        return outInterceptors == NOT_FOUND ? null : outInterceptors;
    }
    
    public void setOutInterceptors(Object outInterceptors) {
        this.outInterceptors = outInterceptors;
    }

    public Object getResponseCode() {
        return responseCode == NOT_FOUND ? null : responseCode;
    }
    
    public void setResponseCode(Object responseCode) {
        this.responseCode = responseCode;
    }

    public Object getEncoding() {
        return encoding == NOT_FOUND ? null : encoding;
    }
    
    public void setEncoding(Object encoding) {
        this.encoding = encoding;
    }

    @SuppressWarnings("rawtypes")
    public Collection getInterceptorProviders() {
        //System.out.println("***JTD: getProtocolHeaders returning " + protoHeaders);
        return interceptorProviders == NOT_FOUND ? null : (Collection) interceptorProviders;
    }
    
    @SuppressWarnings("rawtypes")
    public void setInterceptorProviders(Collection interceptorProviders) {
        //System.out.println("***JTD: setProtocolHeaders setting " + protoHeaders);
        this.interceptorProviders = interceptorProviders;
    }

    //public MultivaluedMap<String, String> getTemplateParameters() {
    public Object getTemplateParameters() {
        //System.out.println("***JTD: getProtocolHeaders returning " + protoHeaders);
        return templateParameters == NOT_FOUND ? null :  templateParameters;
    }
    
    //public void setTemplateParameters(MultivaluedMap<String, String> templateParameters) {    
    public void setTemplateParameters(Object templateParameters) {
        //System.out.println("***JTD: setProtocolHeaders setting " + protoHeaders);
        this.templateParameters = templateParameters;
    }

    public void removeContentType() {
        contentType = NOT_FOUND;
    }
    public void removeHttpResponse() {
        httpResponse = NOT_FOUND;
    }
    public void removeHttpRequest() {
        httpRequest = NOT_FOUND;
    }
    
    @Override
    public Object remove(Object key) {
        //System.out.println("***JTD: removing " + key);
        if (KEYS.contains(key)) {
            Object ret = null;
            if (key == PROTOCOL_HEADERS) {
                ret = protoHeaders;
                protoHeaders = NOT_FOUND;
            } else if (key == CONTENT_TYPE) {
                ret = contentType;
                contentType = NOT_FOUND;
            } else if (key == QUERY_STRING) {
                ret = queryString;
                queryString = NOT_FOUND;
            } else if (key == AbstractHTTPDestination.HTTP_REQUEST) {
                ret = httpRequest;
                httpRequest = NOT_FOUND;
            } else if (key == AbstractHTTPDestination.HTTP_RESPONSE) {
                ret = httpResponse;
                httpResponse = NOT_FOUND;
            } else if (key == REQUEST_PATH_TO_MATCH_SLASH) {
                ret = pathToMatchSlash;
                pathToMatchSlash = NOT_FOUND;
            } else if (key == HTTP_REQUEST_METHOD) {
                ret = httpRequestMethod;
                httpRequestMethod = NOT_FOUND;
            } else if (key == INTERCEPTOR_PROVIDERS) {
                ret = interceptorProviders;
                interceptorProviders = NOT_FOUND;
            } else if (key == TEMPLATE_PARAMETERS) {
                ret = templateParameters;
                templateParameters = NOT_FOUND;
            } else if (key == ACCEPT_CONTENT_TYPE) {
                ret = accept;
                accept = NOT_FOUND;
            } else if (key == CONTINUATION_PROVIDER) {
                ret = continuationProvider;
                continuationProvider = NOT_FOUND;
            } else if (key == OP_RES_INFO_STACK) {
                ret = opStack;
                opStack = NOT_FOUND;
            } else if (key == DESTINATION) {
                ret = destination;
                destination = NOT_FOUND;
            } else if (key == WSDL_DESCRIPTION) {
                ret = wsdlDescription;
                wsdlDescription = NOT_FOUND;
            } else if (key == WSDL_INTERFACE) {
                ret = wsdlInterface;
                wsdlInterface = NOT_FOUND;
            } else if (key == WSDL_OPERATION) {
                ret = wsdlOperation;
                wsdlOperation = NOT_FOUND;
            } else if (key == WSDL_PORT) {
                ret = wsdlPort;
                wsdlPort = NOT_FOUND;
            } else if (key == WSDL_SERVICE) {
                ret = wsdlService;
                wsdlService = NOT_FOUND;
            } else if (key == REQUEST_URL) {
                ret = requestUrl;
                requestUrl = NOT_FOUND;
            } else if (key == REQUEST_URI) {
                ret = requestUri;
                requestUri = NOT_FOUND;
            } else if (key == PATH_INFO) {
                ret = pathInfo;
                pathInfo = NOT_FOUND;
            } else if (key == BASE_PATH) {
                ret = basePath;
                basePath = NOT_FOUND;
            } else if (key == FIXED_PARAMETER_ORDER) {
                ret = fixedParamOrder;
                fixedParamOrder = NOT_FOUND;
            } else if (key == IN_INTERCEPTORS) {
                ret = inInterceptors;
                inInterceptors = NOT_FOUND;
            } else if (key == OUT_INTERCEPTORS) {
                ret = outInterceptors;
                outInterceptors = NOT_FOUND;
            } else if (key == RESPONSE_CODE) {
                ret = responseCode;
                responseCode = NOT_FOUND;
            } else if (key == ATTACHMENTS) {
                ret = attachments;
                attachments = NOT_FOUND;
            } else if (key == ENCODING) {
                ret = encoding;
                encoding = NOT_FOUND;
            }

            return ret == NOT_FOUND ? null : ret;
        }
        return super.remove(key);
    }
    
    public String getPathToMatchSlash() {
        //System.out.println("***JTD: getQueryString returning " + queryString);
        return pathToMatchSlash == NOT_FOUND ? null : (String) pathToMatchSlash;
    }
    
    public void setPathToMatchSlash(String pathToMatchSlash) {
        //System.out.println("***JTD: setQueryString setting " + queryString);
        this.pathToMatchSlash = pathToMatchSlash;
    }
    
    public String getHttpRequestMethod() {
        //System.out.println("***JTD: getQueryString returning " + queryString);
        return httpRequestMethod == NOT_FOUND ? null : (String) httpRequestMethod;
    }
    
    public void setHttpRequestMethod(String httpRequestMethod) {
        //System.out.println("***JTD: setQueryString setting " + queryString);
        this.httpRequestMethod = httpRequestMethod;
    }
    public void removePathToMatchSlash() {
        pathToMatchSlash = NOT_FOUND;
    }
    public String getQueryString() {
        //System.out.println("***JTD: getQueryString returning " + queryString);
        return queryString == NOT_FOUND ? null : (String) queryString;
    }
    
    public void setQueryString(String queryString) {
        //System.out.println("***JTD: setQueryString setting " + queryString);
        this.queryString = queryString;
    }
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> key) {
        return (T) get(key.getName());
    }

    @Override
    public <T> void put(Class<T> key, T value) {
        put(key.getName(), value);
    }

    @Override
    public Object get(Object key) {
        return get((String) key);
    }

    public Object get(String key) {
        //System.out.println("***JTD: get2 " + key);
        if (key.contains("message.Message")) {
            Thread.dumpStack();
        }
        if (KEYS.contains(key)) {
            if (key == PROTOCOL_HEADERS) {
                return getProtocolHeaders();
            } else if (key == CONTENT_TYPE) {
                //System.out.println("***JTD: returningCT " + super.get(key));
                //Thread.dumpStack();
                return getContentType();
            } else if (key == QUERY_STRING) {
                //System.out.println("***JTD: returningQS " + super.get(key));
                //Thread.dumpStack();
                return getQueryString();
            } else if (key == AbstractHTTPDestination.HTTP_REQUEST) {
                //System.out.println("***JTD: returningRE " + super.get(key));
                //Thread.dumpStack();
                return getHttpRequest();
            } else if (key == AbstractHTTPDestination.HTTP_RESPONSE) {
                //System.out.println("***JTD: returningRE " + super.get(key));
                //Thread.dumpStack();
                return getHttpResponse();
            } else if (key == REQUEST_PATH_TO_MATCH_SLASH) {
                //System.out.println("***JTD: returningRE " + super.get(key));
                //Thread.dumpStack();
                return getPathToMatchSlash();
            } else if (key == HTTP_REQUEST_METHOD) {
                //System.out.println("***JTD: returningRE " + super.get(key));
                //Thread.dumpStack();
                return getHttpRequestMethod();
            } else if (key == INTERCEPTOR_PROVIDERS) {
                return getInterceptorProviders();
            } else if (key == TEMPLATE_PARAMETERS) {
                return getTemplateParameters();
            } else if (key == ACCEPT_CONTENT_TYPE) {
                return getAccept();
            } else if (key == CONTINUATION_PROVIDER) {
                return getContinuationProvider();
            } else if (key == OP_RES_INFO_STACK) {
                return getOperationResourceInfoStack();
            } else if (key == DESTINATION) {
                return getDestination();
            } else if (key == WSDL_DESCRIPTION) {
                return getWsdlDescription();
            } else if (key == WSDL_INTERFACE) {
                return getWsdlInterface();
            } else if (key == WSDL_OPERATION) {
                return getWsdlOperation();
            } else if (key == WSDL_PORT) {
                return getWsdlPort();
            } else if (key == WSDL_SERVICE) {
                return getWsdlService();
            } else if (key == REQUEST_URL) {
                return getRequestUrl();
            } else if (key == REQUEST_URI) {
                return getRequestUri();
            } else if (key == PATH_INFO) {
                return getPathInfo();
            } else if (key == BASE_PATH) {
                return getBasePath();
            } else if (key == FIXED_PARAMETER_ORDER) {
                return getFixedParamOrder();
            } else if (key == IN_INTERCEPTORS) {
                return getInInterceptors();
            } else if (key == OUT_INTERCEPTORS) {
                return getOutInterceptors();
            } else if (key == RESPONSE_CODE) {
                return getResponseCode();
            } else if (key == ATTACHMENTS) {
                return getAttachments();
            } else if (key == ENCODING) {
                return getEncoding();
            }
        }
        
        return super.get(key);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object put(String key, Object value) {
        //System.out.println("***JTD: put2 " + key);
        if (KEYS.contains(key)) {
            Object ret = null;
            if (key == PROTOCOL_HEADERS) {
                ret = getProtocolHeaders();
                setProtocolHeaders((Map) value);
            } else if (key == CONTENT_TYPE) {
                ret = getContentType();
                setContentType((String) value);
            } else if (key == QUERY_STRING) {
                ret = getQueryString();
                setQueryString((String) value);
            } else if (key == AbstractHTTPDestination.HTTP_REQUEST) {
                ret = getHttpRequest();
                setHttpRequest(value);
            } else if (key == AbstractHTTPDestination.HTTP_RESPONSE) {
                ret = getHttpResponse();
                setHttpResponse(value);
            } else if (key == REQUEST_PATH_TO_MATCH_SLASH) {
                ret = getPathToMatchSlash();
                setPathToMatchSlash((String) value);
            } else if (key == HTTP_REQUEST_METHOD) {
                ret = getHttpRequestMethod();
                setHttpRequestMethod((String) value);
            } else if (key == INTERCEPTOR_PROVIDERS) {
                ret = getInterceptorProviders();
                setInterceptorProviders((Collection) value);
            } else if (key == TEMPLATE_PARAMETERS) {
                ret = getTemplateParameters();
                setTemplateParameters(value);
            } else if (key == ACCEPT_CONTENT_TYPE) {
                ret = getAccept();
                setAccept(value);
            } else if (key == CONTINUATION_PROVIDER) {
                ret = getContinuationProvider();
                setContinuationProvider(value);
            } else if (key == OP_RES_INFO_STACK) {
                ret = getOperationResourceInfoStack();
                setOperationResourceInfoStack(value);
            } else if (key == DESTINATION) {
                ret = getDestination();
                setDestination((Destination) value);
            } else if (key == WSDL_DESCRIPTION) {
                ret = getWsdlDescription();
                setWsdlDescription(value);
            } else if (key == WSDL_INTERFACE) {
                ret = getWsdlInterface();
                setWsdlInterface(value);
            } else if (key == WSDL_OPERATION) {
                ret = getWsdlOperation();
                setWsdlOperation(value);
            } else if (key == WSDL_PORT) {
                ret = getWsdlPort();
                setWsdlPort(value);
            } else if (key == WSDL_SERVICE) {
                ret = getWsdlService();
                setWsdlService(value);
            } else if (key == REQUEST_URL) {
                ret = getRequestUrl();
                setRequestUrl(value);
            } else if (key == REQUEST_URI) {
                ret = getRequestUri();
                setRequestUri(value);
            } else if (key == PATH_INFO) {
                ret = getPathInfo();
                setPathInfo(value);
            } else if (key == BASE_PATH) {
                ret = getBasePath();
                setBasePath(value);
            } else if (key == FIXED_PARAMETER_ORDER) {
                ret = getFixedParamOrder();
                setFixedParamOrder(value);
            } else if (key == IN_INTERCEPTORS) {
                ret = getInInterceptors();
                setInInterceptors(value);
            } else if (key == OUT_INTERCEPTORS) {
                ret = getOutInterceptors();
                setOutInterceptors(value);
            } else if (key == RESPONSE_CODE) {
                ret = getResponseCode();
                setResponseCode(value);
            } else if (key == ATTACHMENTS) {
                ret = getAttachments();
                setAttachments((Collection<Attachment>) value);
            } else if (key == ENCODING) {
                ret = getEncoding();
                setEncoding(value);
            }
            return ret == NOT_FOUND ? null : ret;
        }

        return super.put(key, value);
    }

    @Override
    public Set<String> keySet() {
        //System.out.println("***JTD: keySet");
        Set<String> keys = super.keySet();
        if (protoHeaders != NOT_FOUND) {
            keys.add(PROTOCOL_HEADERS);
        } 
        if (contentType != NOT_FOUND) {
            keys.add(CONTENT_TYPE);
        }
        if (queryString != NOT_FOUND) {
            keys.add(QUERY_STRING);
        }
        if (httpRequest != NOT_FOUND) {
            keys.add(AbstractHTTPDestination.HTTP_REQUEST);
        }
        if (httpResponse != NOT_FOUND) {
            keys.add(AbstractHTTPDestination.HTTP_RESPONSE);
        }
        if (pathToMatchSlash != NOT_FOUND) {
            keys.add(REQUEST_PATH_TO_MATCH_SLASH);
        }
        if (httpRequestMethod != NOT_FOUND) {
            keys.add(HTTP_REQUEST_METHOD);
        }
        if (interceptorProviders != NOT_FOUND) {
            keys.add(INTERCEPTOR_PROVIDERS);
        }
        if (templateParameters != NOT_FOUND) {
            keys.add(TEMPLATE_PARAMETERS);
        }
        if (accept != NOT_FOUND) {
            keys.add(ACCEPT_CONTENT_TYPE);
        }
        if (continuationProvider != NOT_FOUND) {
            keys.add(CONTINUATION_PROVIDER);
        }
        if (opStack != NOT_FOUND) {
            keys.add(OP_RES_INFO_STACK);
        }
        if (destination != NOT_FOUND) {
            keys.add(DESTINATION);
        }
        if (wsdlDescription != NOT_FOUND) {
            keys.add(WSDL_DESCRIPTION);
        }
        if (wsdlInterface != NOT_FOUND) {
            keys.add(WSDL_INTERFACE);
        }
        if (wsdlOperation != NOT_FOUND) {
            keys.add(WSDL_OPERATION);
        }
        if (wsdlPort != NOT_FOUND) {
            keys.add(WSDL_PORT);
        }
        if (wsdlService != NOT_FOUND) {
            keys.add(WSDL_SERVICE);
        }
        if (requestUrl != NOT_FOUND) {
            keys.add(REQUEST_URL);
        }
        if (requestUri != NOT_FOUND) {
            keys.add(REQUEST_URI);
        }
        if (pathInfo != NOT_FOUND) {
            keys.add(PATH_INFO);
        }
        if (basePath != NOT_FOUND) {
            keys.add(BASE_PATH);
        }
        if (fixedParamOrder != NOT_FOUND) {
            keys.add(FIXED_PARAMETER_ORDER);
        }
        if (inInterceptors != NOT_FOUND) {
            keys.add(IN_INTERCEPTORS);
        }
        if (outInterceptors != NOT_FOUND) {
            keys.add(OUT_INTERCEPTORS);
        }
        if (responseCode != NOT_FOUND) {
            keys.add(RESPONSE_CODE);
        }
        if (attachments != NOT_FOUND) {
            keys.add(ATTACHMENTS);
        }
        if (encoding != NOT_FOUND) {
            keys.add(ENCODING);
        }
        return keys;
    }
    
    @Override
    public Set<Map.Entry<String,Object>> entrySet() {
        //JTD - how do we do this one???
        //System.out.println("***JTD: entrySet");
        return super.entrySet();
    }
    
    @Override
    public boolean containsKey(Object key) {
        //System.out.println("***JTD: containsKey");
        if (KEYS.contains(key)) {
            if (key == PROTOCOL_HEADERS) {
                return protoHeaders != NOT_FOUND;
            } else if (key == CONTENT_TYPE) {
                //System.out.println("***JTD: returningCT " + super.get(key));
                //Thread.dumpStack();
                return contentType != NOT_FOUND;
            } else if (key == QUERY_STRING) {
                //System.out.println("***JTD: returningQS " + super.get(key));
                //Thread.dumpStack();
                return queryString != NOT_FOUND;
            } else if (key == AbstractHTTPDestination.HTTP_REQUEST) {
                //System.out.println("***JTD: returningRE " + super.get(key));
                //Thread.dumpStack();
                return httpRequest != NOT_FOUND;
            } else if (key == AbstractHTTPDestination.HTTP_RESPONSE) {
                //System.out.println("***JTD: returningRE " + super.get(key));
                //Thread.dumpStack();
                return httpResponse != NOT_FOUND;
            } else if (key == REQUEST_PATH_TO_MATCH_SLASH) {
                //System.out.println("***JTD: returningRE " + super.get(key));
                //Thread.dumpStack();
                return pathToMatchSlash != NOT_FOUND;
            } else if (key == HTTP_REQUEST_METHOD) {
                //System.out.println("***JTD: returningRE " + super.get(key));
                //Thread.dumpStack();
                return httpRequestMethod != NOT_FOUND;
            } else if (key == INTERCEPTOR_PROVIDERS) {
                return interceptorProviders != NOT_FOUND;
            } else if (key == TEMPLATE_PARAMETERS) {
                return templateParameters != NOT_FOUND;
            } else if (key == ACCEPT_CONTENT_TYPE) {
                return accept != NOT_FOUND;
            } else if (key == CONTINUATION_PROVIDER) {
                return continuationProvider != NOT_FOUND;
            } else if (key == OP_RES_INFO_STACK) {
                return opStack != NOT_FOUND;
            } else if (key == DESTINATION) {
                return destination != NOT_FOUND;
            } else if (key == WSDL_DESCRIPTION) {
                return wsdlDescription != NOT_FOUND;
            } else if (key == WSDL_INTERFACE) {
                return wsdlInterface != NOT_FOUND;
            } else if (key == WSDL_OPERATION) {
                return wsdlOperation != NOT_FOUND;
            } else if (key == WSDL_PORT) {
                return wsdlPort != NOT_FOUND;
            } else if (key == WSDL_SERVICE) {
                return wsdlService != NOT_FOUND;
            } else if (key == REQUEST_URL) {
                return requestUrl != NOT_FOUND;
            } else if (key == REQUEST_URI) {
                return requestUri != NOT_FOUND;
            } else if (key == PATH_INFO) {
                return pathInfo != NOT_FOUND;
            } else if (key == BASE_PATH) {
                return basePath != NOT_FOUND;
            } else if (key == FIXED_PARAMETER_ORDER) {
                return fixedParamOrder != NOT_FOUND;
            } else if (key == IN_INTERCEPTORS) {
                return inInterceptors != NOT_FOUND;
            } else if (key == OUT_INTERCEPTORS) {
                return outInterceptors != NOT_FOUND;
            } else if (key == RESPONSE_CODE) {
                return responseCode != NOT_FOUND;
            } else if (key == ATTACHMENTS) {
                return attachments != NOT_FOUND;
            } else if (key == ENCODING) {
                return encoding != NOT_FOUND;
            }
        }
        return super.containsKey(key);
    }
    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        //System.out.println("***JTD: putAll");
        if (m.containsKey(PROTOCOL_HEADERS)) {
            protoHeaders = m.get(PROTOCOL_HEADERS);
        }
        if (m.containsKey(CONTENT_TYPE)) {
            contentType = m.get(CONTENT_TYPE);
        }
        if (m.containsKey(QUERY_STRING)) {
            queryString = m.get(QUERY_STRING);
        }
        if (m.containsKey(AbstractHTTPDestination.HTTP_REQUEST)) {
            httpRequest = m.get(AbstractHTTPDestination.HTTP_REQUEST);
        }
        if (m.containsKey(AbstractHTTPDestination.HTTP_RESPONSE)) {
            httpResponse = m.get(AbstractHTTPDestination.HTTP_RESPONSE);
        }
        if (m.containsKey(REQUEST_PATH_TO_MATCH_SLASH)) {
            pathToMatchSlash = m.get(REQUEST_PATH_TO_MATCH_SLASH);
        }
        if (m.containsKey(HTTP_REQUEST_METHOD)) {
            httpRequestMethod = m.get(HTTP_REQUEST_METHOD);
        }
        if (m.containsKey(INTERCEPTOR_PROVIDERS)) {
            interceptorProviders = m.get(INTERCEPTOR_PROVIDERS);
        }
        if (m.containsKey(TEMPLATE_PARAMETERS)) {
            templateParameters = m.get(TEMPLATE_PARAMETERS);
        }
        if (m.containsKey(ACCEPT_CONTENT_TYPE)) {
            accept = m.get(ACCEPT_CONTENT_TYPE);
        }
        if (m.containsKey(CONTINUATION_PROVIDER)) {
            continuationProvider = m.get(CONTINUATION_PROVIDER);
        }
        if (m.containsKey(OP_RES_INFO_STACK)) {
            opStack = m.get(OP_RES_INFO_STACK);
        }
        if (m.containsKey(DESTINATION)) {
            destination = m.get(DESTINATION);
        }
        if (m.containsKey(WSDL_DESCRIPTION)) {
            wsdlDescription = m.get(WSDL_DESCRIPTION);
        }
        if (m.containsKey(WSDL_INTERFACE)) {
            wsdlInterface = m.get(WSDL_INTERFACE);
        }
        if (m.containsKey(WSDL_OPERATION)) {
            wsdlOperation = m.get(WSDL_OPERATION);
        }
        if (m.containsKey(WSDL_PORT)) {
            wsdlPort = m.get(WSDL_PORT);
        }
        if (m.containsKey(WSDL_SERVICE)) {
            wsdlService = m.get(WSDL_SERVICE);
        }
        if (m.containsKey(REQUEST_URL)) {
            requestUrl = m.get(REQUEST_URL);
        }
        if (m.containsKey(REQUEST_URI)) {
            requestUri = m.get(REQUEST_URI);
        }
        if (m.containsKey(PATH_INFO)) {
            pathInfo = m.get(PATH_INFO);
        }
        if (m.containsKey(BASE_PATH)) {
            basePath = m.get(BASE_PATH);
        }
        if (m.containsKey(FIXED_PARAMETER_ORDER)) {
            fixedParamOrder = m.get(FIXED_PARAMETER_ORDER);
        }
        if (m.containsKey(IN_INTERCEPTORS)) {
            inInterceptors = m.get(IN_INTERCEPTORS);
        }
        if (m.containsKey(OUT_INTERCEPTORS)) {
            outInterceptors = m.get(OUT_INTERCEPTORS);
        }
        if (m.containsKey(RESPONSE_CODE)) {
            responseCode = m.get(RESPONSE_CODE);
        }
        if (m.containsKey(ATTACHMENTS)) {
            attachments = m.get(ATTACHMENTS);
        }
        if (m.containsKey(ENCODING)) {
            encoding = m.get(ENCODING);
        }
        super.putAll(m);
    }
    @Override
    public Collection<Object> values() {
        //System.out.println("***JTD: values");
        Collection<Object> values = super.values();
        if (protoHeaders != NOT_FOUND) {
            values.add(protoHeaders);
        } 
        if (contentType != NOT_FOUND) {
            values.add(contentType);
        }
        if (queryString != NOT_FOUND) {
            values.add(queryString);
        }
        if (httpRequest != NOT_FOUND) {
            values.add(httpRequest);
        }
        if (httpResponse != NOT_FOUND) {
            values.add(httpResponse);
        }
        if (pathToMatchSlash != NOT_FOUND) {
            values.add(pathToMatchSlash);
        }
        if (httpRequestMethod != NOT_FOUND) {
            values.add(httpRequestMethod);
        }
        if (interceptorProviders != NOT_FOUND) {
            values.add(interceptorProviders);
        }
        if (templateParameters != NOT_FOUND) {
            values.add(templateParameters);
        }
        if (accept != NOT_FOUND) {
            values.add(accept);
        }
        if (continuationProvider != NOT_FOUND) {
            values.add(continuationProvider);
        }
        if (opStack != NOT_FOUND) {
            values.add(opStack);
        }
        if (destination != NOT_FOUND) {
            values.add(destination);
        }
        if (wsdlDescription != NOT_FOUND) {
            values.add(wsdlDescription);
        }
        if (wsdlInterface != NOT_FOUND) {
            values.add(wsdlInterface);
        }
        if (wsdlOperation != NOT_FOUND) {
            values.add(wsdlOperation);
        }
        if (wsdlPort != NOT_FOUND) {
            values.add(wsdlPort);
        }
        if (wsdlService != NOT_FOUND) {
            values.add(wsdlService);
        }
        if (requestUrl != NOT_FOUND) {
            values.add(requestUrl);
        }
        if (requestUri != NOT_FOUND) {
            values.add(requestUri);
        }
        if (pathInfo != NOT_FOUND) {
            values.add(pathInfo);
        }
        if (basePath != NOT_FOUND) {
            values.add(basePath);
        }
        if (fixedParamOrder != NOT_FOUND) {
            values.add(fixedParamOrder);
        }
        if (inInterceptors != NOT_FOUND) {
            values.add(inInterceptors);
        }
        if (outInterceptors != NOT_FOUND) {
            values.add(outInterceptors);
        }
        if (responseCode != NOT_FOUND) {
            values.add(responseCode);
        }
        if (attachments != NOT_FOUND) {
            values.add(attachments);
        }
        if (encoding != NOT_FOUND) {
            values.add(encoding);
        }
        return values;
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    public InterceptorChain getInterceptorChain() {
        return this.interceptorChain;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getContent(Class<T> format) {
        for (int x = 0; x < index; x += 2) {
            if (contents[x] == format) {
                return (T) contents[x + 1];
            }
        }
        return null;
    }

    @Override
    public <T> void setContent(Class<T> format, Object content) {
        for (int x = 0; x < index; x += 2) {
            if (contents[x] == format) {
                contents[x + 1] = content;
                return;
            }
        }
        if (index >= contents.length) {
            //very unlikely to happen.   Haven't seen more than about 6,
            //but just in case we'll add a few more
            Object[] tmp = new Object[contents.length + 10];
            System.arraycopy(contents, 0, tmp, 0, contents.length);
            contents = tmp;
        }
        contents[index] = format;
        contents[index + 1] = content;
        index += 2;
    }

    @Override
    public <T> void removeContent(Class<T> format) {
        for (int x = 0; x < index; x += 2) {
            if (contents[x] == format) {
                index -= 2;
                if (x != index) {
                    contents[x] = contents[index];
                    contents[x + 1] = contents[index + 1];
                }
                contents[index] = null;
                contents[index + 1] = null;
                return;
            }
        }
    }

    @Override
    public Set<Class<?>> getContentFormats() {

        Set<Class<?>> c = new HashSet<>();
        for (int x = 0; x < index; x += 2) {
            c.add((Class<?>) contents[x]);
        }
        return c;
    }

    public void setDestination(Destination d) {
        //System.out.println("***JTD: puttingDest " + d);
        //Thread.dumpStack();

        destination = d;
        //super.put(Destination.class, d);
    }

    @Override
    public void setExchange(Exchange e) {
        this.exchange = e;
    }

    @Override
    public void setId(String i) {
        this.id = i;
    }

    @Override
    public void setInterceptorChain(InterceptorChain ic) {
        this.interceptorChain = ic;
    }

    //Liberty code change start
    // Since these maps can have null value, use the getOrDefault API
    // to prevent calling get twice under the covers
    private static final Object NOT_FOUND = new Object();
    
    @Override
    public Object getContextualProperty(String key) {
        //System.out.println("***JTD: getContextualProperty " + key);
        Object o = null;
        if (KEYS.contains(key)) {
            if (key == PROTOCOL_HEADERS) {
                if (protoHeaders != NOT_FOUND) {
                    return protoHeaders;
                }
            } else if (key == CONTENT_TYPE) {
                if (contentType != NOT_FOUND) {
                    return contentType;
                }
            } else if (key == QUERY_STRING) {
                if (queryString != NOT_FOUND) {
                    return queryString;
                }
            } else if (key == AbstractHTTPDestination.HTTP_REQUEST) {
                if (httpRequest != NOT_FOUND) {
                    return httpRequest;
                }
            } else if (key == AbstractHTTPDestination.HTTP_RESPONSE) {
                if (httpResponse != NOT_FOUND) {
                    return httpResponse;
                }
            } else if (key == REQUEST_PATH_TO_MATCH_SLASH) {
                if (pathToMatchSlash != NOT_FOUND) {
                    return pathToMatchSlash;
                }
            } else if (key == HTTP_REQUEST_METHOD) {
                if (httpRequestMethod != NOT_FOUND) {
                    return httpRequestMethod;
                }
            } else if (key == INTERCEPTOR_PROVIDERS) {
                if (interceptorProviders != NOT_FOUND) {
                    return interceptorProviders;
                }
            } else if (key == TEMPLATE_PARAMETERS) {
                if (templateParameters != NOT_FOUND) {
                    return templateParameters;
                }
            } else if (key == ACCEPT_CONTENT_TYPE) {
                if (accept != NOT_FOUND) {
                    return accept;
                }
            } else if (key == CONTINUATION_PROVIDER) {
                if (continuationProvider != NOT_FOUND) {
                    return continuationProvider;
                }
            } else if (key == OP_RES_INFO_STACK) {
                if (opStack != NOT_FOUND) {
                    return opStack;
                }
            } else if (key == DESTINATION) {
                if (destination != NOT_FOUND) {
                    return destination;
                }
            } else if (key == WSDL_DESCRIPTION) {
                if (wsdlDescription != NOT_FOUND) {
                    return wsdlDescription;
                }
            } else if (key == WSDL_INTERFACE) {
                if (wsdlInterface != NOT_FOUND) {
                    return wsdlInterface;
                }
            } else if (key == WSDL_OPERATION) {
                if (wsdlOperation != NOT_FOUND) {
                    return wsdlOperation;
                }
            } else if (key == WSDL_PORT) {
                if (wsdlPort != NOT_FOUND) {
                    return wsdlPort;
                }
            } else if (key == WSDL_SERVICE) {
                if (wsdlService != NOT_FOUND) {
                    return wsdlService;
                }
            } else if (key == REQUEST_URL) {
                if (requestUrl != NOT_FOUND) {
                    return requestUrl;
                }
            } else if (key == REQUEST_URI) {
                if (requestUri != NOT_FOUND) {
                    return requestUri;
                }
            } else if (key == PATH_INFO) {
                if (pathInfo != NOT_FOUND) {
                    return pathInfo;
                }
            } else if (key == BASE_PATH) {
                if (basePath != NOT_FOUND) {
                    return basePath;
                }
            } else if (key == FIXED_PARAMETER_ORDER) {
                if (fixedParamOrder != NOT_FOUND) {
                    return fixedParamOrder;
                }
            } else if (key == IN_INTERCEPTORS) {
                if (inInterceptors != NOT_FOUND) {
                    return inInterceptors;
                }
            } else if (key == OUT_INTERCEPTORS) {
                if (outInterceptors != NOT_FOUND) {
                    return outInterceptors;
                }
            } else if (key == RESPONSE_CODE) {
                if (responseCode != NOT_FOUND) {
                    return responseCode;
                }
            } else if (key == ATTACHMENTS) {
                if (attachments != NOT_FOUND) {
                    return attachments;
                }
            } else if (key == ENCODING) {
                if (encoding != NOT_FOUND) {
                    return encoding;
                }
            }
        }

        o = getOrDefault(key, NOT_FOUND);
        if (o != NOT_FOUND) {
            return o;
        }
        return getFromExchange(key);
    }

    private Object getFromExchange(String key) {
        Exchange ex = getExchange();
        if (ex != null) {
            Object o = ex.getOrDefault(key, NOT_FOUND);
            if (o != NOT_FOUND) {
                return o;
            }
            
            Map<String, Object> p;
            Endpoint ep = ex.getEndpoint();
            if (ep != null) {
                o = ep.getOrDefault(key, NOT_FOUND);
                if (o != NOT_FOUND) {
                    return o;
                }

                EndpointInfo ei = ep.getEndpointInfo();
                if (ei != null) {
                    if ((p = ei.getProperties()) != null && (o = p.getOrDefault(key, NOT_FOUND)) != NOT_FOUND) {
                        return o;
                    }
                    if ((p = ei.getBinding().getProperties()) != null && (o = p.getOrDefault(key, NOT_FOUND)) != NOT_FOUND) {
                        return o;
                    }
                }
            }
            Service sv = ex.getService();
            if (sv != null && (o = sv.getOrDefault(key, NOT_FOUND)) != NOT_FOUND) {
                return o;
            }
            Bus b = ex.getBus();
            if (b != null && (p = b.getProperties()) != null) {
                if ((o = p.getOrDefault(key, NOT_FOUND)) != NOT_FOUND) {
                    return o;
                }
            }
        }
        return null;
    }

    private Set<String> getExchangeKeySet() {
        HashSet<String> keys = new HashSet<>();
        Exchange ex = getExchange();
        if (ex != null) {
            Bus b = ex.getBus();
            Map<String, Object> p;
            if (b != null && (p = b.getProperties()) != null) {
                if (!p.isEmpty()) {
                    keys.addAll(p.keySet());
                }
            }
            Service sv = ex.getService();
            if (sv != null && !sv.isEmpty()) {
                keys.addAll(sv.keySet());
            }
            Endpoint ep = ex.getEndpoint();
            if (ep != null) {
                EndpointInfo ei = ep.getEndpointInfo();
                if (ei != null) {
                    if ((p = ei.getBinding().getProperties()) != null) {
                        if (!p.isEmpty()) {
                            keys.addAll(p.keySet());
                        }
                    }
                    if ((p = ei.getProperties()) != null) {
                        if (!p.isEmpty()) {
                            keys.addAll(p.keySet());
                        }
                    }
                }
                
                if (!ep.isEmpty()) {
                    keys.addAll(ep.keySet());
                }
            }
            if (!ex.isEmpty()) {
                keys.addAll(ex.keySet());
            }
        }
        return keys;
    }

    @Override
    public Set<String> getContextualPropertyKeys() {
        //System.out.println("***JTD: getContextualPropertyKeys");
        Set<String> s = getExchangeKeySet();
        s.addAll(keySet());
        return s;
    }
    //Liberty code change end
    
    public static void copyContent(Message m1, Message m2) {
        for (Class<?> c : m1.getContentFormats()) {
            m2.setContent(c, m1.getContent(c));
        }
    }

    //Liberty code change start
    @Override
    public void resetContextCache() {
    }

    void setContextualProperty(String key, Object v) {
        //System.out.println("***JTD: setContextualProperty " + key);

        if (!containsKey(key)) {
            put(key, v);
        }
    }
    //Liberty code change end
}
